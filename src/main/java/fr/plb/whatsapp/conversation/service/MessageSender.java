package fr.plb.whatsapp.conversation.service;

import fr.plb.whatsapp.conversation.dto.message.MessageDTO;
import fr.plb.whatsapp.user.entity.User;
import fr.plb.whatsapp.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class MessageSender {

    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);

    private final UserService userService;

    private final Map<String, Sinks.Many<ServerSentEvent<MessageDTO>>> emitters = new HashMap<>();

    public MessageSender(UserService userService) {
        this.userService = userService;
    }

    private Flux<ServerSentEvent<MessageDTO>> keepAlive(Duration duration, Sinks.Many<ServerSentEvent<MessageDTO>> newSink) {
        return Flux.interval(duration)
                .map(e -> ServerSentEvent.<MessageDTO>builder()
                        .comment("keep alive")
                        .build());
    }

    public Flux<ServerSentEvent<MessageDTO>> subscribe() {
        Sinks.Many<ServerSentEvent<MessageDTO>> newSink = Sinks.many().unicast().onBackpressureBuffer();

        Flux<ServerSentEvent<MessageDTO>> newMessageStream = userService.getConnectedUser()
                .doOnNext(connectedUser -> this.emitters.put(connectedUser.getId(), newSink))
                .flatMapMany(connectedUser -> newSink.asFlux()
                        .doOnCancel(() -> this.emitters.remove(connectedUser.getId())))
                .limitRate(1)
                .doFinally(event -> newSink.tryEmitComplete());

        return Flux.merge(newMessageStream, keepAlive(Duration.ofSeconds(2), newSink));
    }

    public Mono<MessageDTO> send(Set<User> members, MessageDTO message) {
        return Flux.fromIterable(members)
                .filter(user -> !message.getSenderId().equals(user.getId()))
                .filter(user -> emitters.containsKey(user.getId()))
                .flatMap(user -> {
                    Sinks.Many<ServerSentEvent<MessageDTO>> sink = emitters.get(user.getId());
                    ServerSentEvent<MessageDTO> newMessage = ServerSentEvent.builder(message).build();
                    return Mono.fromRunnable(() -> {
                        Sinks.EmitResult result = sink.tryEmitNext(newMessage);
                        if (result.isSuccess()) {
                            log.info("Message sent to user id: {}", user.getId());
                        } else {
                            log.warn("Failed to send message to user id: {}. Emit result was: {}", user.getId(), result);
                        }
                    });
                })
                .then(Mono.just(message))
                .onErrorResume(error -> {
                    log.error("Failed to send message due to error: {}", error.getMessage(), error);
                    return Mono.empty();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .log();
    }
}

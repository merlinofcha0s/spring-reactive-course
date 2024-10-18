package fr.plb.whatsapp.conversation.resource;

import fr.plb.whatsapp.conversation.dto.message.MessageDTO;
import fr.plb.whatsapp.conversation.dto.message.MessageSendNewDTO;
import fr.plb.whatsapp.conversation.service.MessageSender;
import fr.plb.whatsapp.conversation.service.MessageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageResource {

    private final MessageService messageService;
    private final MessageSender messageSender;

    public MessageResource(MessageService messageService, MessageSender messageSender) {
        this.messageService = messageService;
        this.messageSender = messageSender;
    }

    @PostMapping(value = "/send")
    public Mono<ResponseEntity<MessageDTO>> send(@RequestBody MessageSendNewDTO messageSendNewDTO) {
        return messageService.createAndSend(messageSendNewDTO)
                .map(message -> ResponseEntity.ok().body(message));
    }

    @PostMapping(value = "/subscribe")
    public Flux<ServerSentEvent<MessageDTO>> subscribe() {
        return messageSender.subscribe();
    }
}

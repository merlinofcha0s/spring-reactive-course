package fr.plb.whatsapp.conversation.service;

import fr.plb.whatsapp.conversation.dto.message.MessageDTO;
import fr.plb.whatsapp.conversation.dto.message.MessageSendNewDTO;
import fr.plb.whatsapp.conversation.entity.*;
import fr.plb.whatsapp.conversation.repository.MessageRepository;
import fr.plb.whatsapp.user.entity.UserBuilder;
import fr.plb.whatsapp.user.service.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ConversationService conversationService;
    private final MessageSender messageSender;

    public MessageService(MessageRepository messageRepository, UserService userService, ConversationService conversationService, MessageSender messageSender) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.conversationService = conversationService;
        this.messageSender = messageSender;
    }

    public Mono<MessageDTO> createAndSend(MessageSendNewDTO messageDTO) {
        return conversationService.findOne(messageDTO.conversationPublicId())
                .zipWith(userService.getConnectedUser())
                .flatMap(conversationAndUser -> {
                    Message newMessage = MessageBuilder.message()
                            .conversation(ConversationBuilder.conversation()
                                    .id(conversationAndUser.getT1().getId()).build())
                            .sender(UserBuilder.user()
                                    .id(conversationAndUser.getT2().getId()).build())
                            .text(messageDTO.content().text())
                            .sendState(MessageSendState.TO_SEND)
                            .sendTime(Instant.now())
                            .type(MessageType.TEXT)
                            .build();
                    return Mono.zip(Mono.just(newMessage),
                            Mono.just(conversationAndUser.getT1().getUsers()));
                })
                .flatMap(conversationAndUser ->
                        Mono.zip(messageRepository.save(conversationAndUser.getT1()), Mono.just(conversationAndUser.getT2())))
                .flatMap(newMessageAndConversationMembers ->
                        messageSender.send(newMessageAndConversationMembers.getT2()
                                , MessageDTO.from(newMessageAndConversationMembers.getT1())))
                .log()
                .subscribeOn(Schedulers.boundedElastic());
    }
}

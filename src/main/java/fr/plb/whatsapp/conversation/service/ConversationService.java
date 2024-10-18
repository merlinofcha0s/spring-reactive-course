package fr.plb.whatsapp.conversation.service;

import fr.plb.whatsapp.conversation.dto.conversation.ConversationToCreateDTO;
import fr.plb.whatsapp.conversation.entity.Conversation;
import fr.plb.whatsapp.conversation.entity.ConversationBuilder;
import fr.plb.whatsapp.conversation.exception.ConversationAlreadyExist;
import fr.plb.whatsapp.conversation.exception.ConversationNotFoundException;
import fr.plb.whatsapp.conversation.repository.ConversationRepository;
import fr.plb.whatsapp.user.entity.User;
import fr.plb.whatsapp.user.entity.UserBuilder;
import fr.plb.whatsapp.user.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final MessageReader messageReader;

    public ConversationService(ConversationRepository conversationRepository, UserService userService, MessageReader messageReader) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
        this.messageReader = messageReader;
    }

    public Mono<Conversation> create(ConversationToCreateDTO conversationToCreateDTO) {
        return userService.getConnectedUser()
                .doOnNext(connectedUser -> conversationToCreateDTO.members().add(connectedUser.getId()))
                .flatMap(users -> conversationRepository.findOneByName(conversationToCreateDTO.name()))
                .flatMap(conversationExist -> Mono.error(new ConversationAlreadyExist("Conversation already exist " + conversationToCreateDTO.name())))
                .then(Mono.just(mapToConversationEntity(conversationToCreateDTO)))
                .flatMap(conversationRepository::save);
    }

    private Conversation mapToConversationEntity(ConversationToCreateDTO conversationToCreateDTO) {
        Set<User> userWithIds = conversationToCreateDTO
                .members()
                .stream()
                .map(id -> UserBuilder.user().id(id).build())
                .collect(Collectors.toSet());

        return ConversationBuilder
                .conversation()
                .name(conversationToCreateDTO.name())
                .users(userWithIds)
                .build();
    }

    public Flux<Conversation> findAll() {
        return userService.getConnectedUser()
                .flatMapMany(connectedUser -> conversationRepository.findAllByUsersId(connectedUser.getId()))
                .log();
    }

    public Mono<Long> delete(String conversationId) {
        return userService.getConnectedUser()
                .flatMap(connectedUser -> conversationRepository
                        .deleteByUsersIdAndId(new ObjectId(connectedUser.getId()), new ObjectId(conversationId)));
    }

    public Mono<Conversation> findOne(String publicId) {
        return userService.getConnectedUser()
                .flatMap(connectedUser -> conversationRepository.findOneByUserIdAndId(connectedUser.getId(), publicId))
                .flatMap(conversation -> messageReader.findAllByConversationId(conversation.getId())
                        .collectList()
                        .map(messages -> {
                            conversation.setMessages(new HashSet<>(messages));
                            return conversation;
                        }))
                .onErrorResume(ConversionFailedException.class, e -> Mono.empty())
                .switchIfEmpty(Mono.error(new ConversationNotFoundException("Conversation not found")));
    }
}

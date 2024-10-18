package fr.plb.whatsapp.conversation.service;

import fr.plb.whatsapp.conversation.entity.Message;
import fr.plb.whatsapp.conversation.repository.MessageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MessageReader {

    private final MessageRepository messageRepository;

    public MessageReader(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Flux<Message> findAllByConversationId(String conversationId) {
        return messageRepository.findAllByConversationId(conversationId);
    }

}

package fr.plb.whatsapp.conversation.repository;


import fr.plb.whatsapp.conversation.entity.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {

    Flux<Message> findAllByConversationId(String id);

}

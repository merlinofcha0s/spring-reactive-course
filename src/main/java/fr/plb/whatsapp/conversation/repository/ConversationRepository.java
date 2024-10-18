package fr.plb.whatsapp.conversation.repository;

import fr.plb.whatsapp.conversation.entity.Conversation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ConversationRepository extends ReactiveMongoRepository<Conversation, String> {

    Flux<Conversation> findAllByUsersId(String publicId);

    @DeleteQuery(value = "{'users._id':  ?0, '_id': ?1 }")
    Mono<Long> deleteByUsersIdAndId(ObjectId userPublicId, ObjectId conversationPublicId);

    @Query("{ 'users._id':  ?0, '_id': ?1 }")
    Mono<Conversation> findOneByUserIdAndId(String userPublicId, String conversationPublicId);

    Mono<Conversation> findOneByName(String name);
}

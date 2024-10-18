package fr.plb.whatsapp.user.repository;

import fr.plb.whatsapp.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByEmail(String email);

    @Query("SELECT user FROM UserEntity user WHERE lower(user.lastName) LIKE lower(concat('%', :query, '%')) " +
            "OR lower(user.firstName) LIKE lower(concat('%', :query, '%'))")
    Flux<User> search(Pageable pageable, String query);

    Flux<User> findByIdIn(Set<String> publicIds);

    @Query("UPDATE UserEntity user SET user.lastSeen = :lastSeen WHERE user.publicId = :userPublicID")
    Mono<Integer> updateLastSeen(UUID userPublicID, Instant lastSeen);

//    Flux<UserEntity> findByConversationsPublicIdAndPublicIdIsNot(UUID conversationsPublicId, UUID publicIdToExclude);

    Mono<User> findOneByEmailIgnoreCase(String email);
}

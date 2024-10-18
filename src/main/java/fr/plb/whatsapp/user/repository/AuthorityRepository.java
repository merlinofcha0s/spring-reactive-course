package fr.plb.whatsapp.user.repository;

import fr.plb.whatsapp.user.entity.Authority;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AuthorityRepository extends ReactiveMongoRepository<Authority, String> {
}

package fr.plb.whatsapp.user.service;

import fr.plb.whatsapp.configuration.security.AuthoritiesConstants;
import fr.plb.whatsapp.configuration.security.SecurityUtils;
import fr.plb.whatsapp.user.dto.UserDTO;
import fr.plb.whatsapp.user.entity.Authority;
import fr.plb.whatsapp.user.entity.User;
import fr.plb.whatsapp.user.exceptions.EmailAlreadyUsedException;
import fr.plb.whatsapp.user.repository.AuthorityRepository;
import fr.plb.whatsapp.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Transactional
    public Mono<User> registerUser(UserDTO userDTO, String password) {
        return userRepository.findOneByEmailIgnoreCase(userDTO.getEmail())
                .flatMap(existingUser -> Mono.error(new EmailAlreadyUsedException()))
                .then(
                        Mono.fromCallable(() -> {
                            User newUser = new User();
                            String encryptedPassword = passwordEncoder.encode(password);
                            newUser.setPassword(encryptedPassword);
                            newUser.setFirstName(userDTO.getFirstName());
                            newUser.setLastName(userDTO.getLastName());
                            if (userDTO.getEmail() != null) {
                                newUser.setEmail(userDTO.getEmail().toLowerCase());
                            }
                            newUser.setImageUrl(userDTO.getImageUrl());
                            return newUser;
                        })
                )
                .flatMap(newUser -> {
                    Set<Authority> authorities = new HashSet<>();
                    return authorityRepository
                            .findById(AuthoritiesConstants.USER)
                            .map(authorities::add)
                            .thenReturn(newUser)
                            .doOnNext(user -> user.setAuthorities(authorities))
                            .flatMap(this::saveUser)
                            .doOnNext(user -> log.debug("Created Information for User: {}", user));
                });
    }

    @Transactional
    public Mono<User> saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mono<User> getConnectedUser() {
        return SecurityUtils.getCurrentUserEmail().flatMap(userRepository::findOneByEmailIgnoreCase);
    }

    public Flux<User> getUsersByPublicId(Set<String> publicIds) {
        return userRepository.findByIdIn(publicIds);
    }
}

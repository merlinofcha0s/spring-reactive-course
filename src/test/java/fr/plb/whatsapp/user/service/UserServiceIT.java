package fr.plb.whatsapp.user.service;

import fr.plb.whatsapp.IntegrationTest;
import fr.plb.whatsapp.user.dto.UserDTO;
import fr.plb.whatsapp.user.entity.User;
import fr.plb.whatsapp.user.exceptions.EmailAlreadyUsedException;
import fr.plb.whatsapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceIT extends IntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    private final static String DEFAULT_EMAIL = "johnathan.doe@example.com";
    private final static String DEFAULT_PASSWORD = "password";

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll().block();
    }

    @Test
    void testRegisterUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail(DEFAULT_EMAIL);

        Mono<User> userRegistration = userService.registerUser(userDTO, DEFAULT_PASSWORD);

        StepVerifier.create(userRegistration)
                .expectNextMatches(user -> user.getEmail().equals(DEFAULT_EMAIL))
                .verifyComplete();

        StepVerifier.create(userRegistration)
                .expectError(EmailAlreadyUsedException.class)
                .verify();
    }

    @Test
    void testGetAll() {
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Smith");
        user1.setEmail(DEFAULT_EMAIL);
        user1.setPassword(passwordEncoder.encode("password"));

        String emailDefaultUser2 = "alice.johnson@example.com";

        User user2 = new User();
        user2.setFirstName("Alice");
        user2.setLastName("Johnson");
        user2.setEmail(emailDefaultUser2);
        user2.setPassword(passwordEncoder.encode("password"));

        userService.saveUser(user1)
                .then(userService.saveUser(user2))
                .block();

        StepVerifier.create(userService.getAll())
                .assertNext(userToVerify ->
                        assertThat(userToVerify.getEmail()).isEqualTo(DEFAULT_EMAIL))
                .assertNext(userToVerify ->
                        assertThat(userToVerify.getEmail()).isEqualTo(emailDefaultUser2))
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = DEFAULT_EMAIL)
    void testGetConnectedUser() {
        User user = new User();
        user.setFirstName("Bob");
        user.setLastName("Marley");
        user.setEmail(DEFAULT_EMAIL);
        user.setPassword(passwordEncoder.encode("password"));

        userService.saveUser(user).block();

        StepVerifier.create(userService.getConnectedUser())
                .expectNextMatches(connectedUser -> connectedUser
                        .getEmail().equals(DEFAULT_EMAIL))
                .verifyComplete();
    }

    @Test
    void testGetUsersByPublicId() {
        User user1 = new User();
        user1.setFirstName("Charlie");
        user1.setLastName("Brown");
        user1.setEmail("charlie.brown@example.com");
        user1.setPassword(passwordEncoder.encode("password"));

        User user2 = new User();
        user2.setFirstName("Lucy");
        user2.setLastName("Van Pelt");
        user2.setEmail("lucy.vanpelt@example.com");
        user2.setPassword(passwordEncoder.encode("password"));

        userService.saveUser(user1)
                .then(userService.saveUser(user2))
                .block();

        Set<String> publicIds = new HashSet<>();
        publicIds.add(user1.getId());
        publicIds.add(user2.getId());

        StepVerifier.create(userService.getUsersByPublicId(publicIds))
                .expectNextCount(2)
                .verifyComplete();
    }
}

package fr.plb.whatsapp.user.resource;

import fr.plb.whatsapp.user.dto.UserDTO;
import fr.plb.whatsapp.user.exceptions.AccountResourceException;
import fr.plb.whatsapp.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public Mono<UserDTO> getAccount() {
        return userService
                .getConnectedUser()
                .map(UserDTO::new)
                .switchIfEmpty(Mono.error(new AccountResourceException("User could not be found")));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<UserDTO>>> getAll() {
        return Mono.just(ResponseEntity.ok(userService.getAll().map(UserDTO::new))
        );
    }
}

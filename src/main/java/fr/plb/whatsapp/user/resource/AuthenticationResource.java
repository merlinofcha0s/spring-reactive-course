package fr.plb.whatsapp.user.resource;

import fr.plb.whatsapp.user.dto.JWTTokenDTO;
import fr.plb.whatsapp.user.dto.LoginDTO;
import fr.plb.whatsapp.user.dto.UserDTO;
import fr.plb.whatsapp.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import static fr.plb.whatsapp.configuration.security.SecurityUtils.AUTHORITIES_KEY;
import static fr.plb.whatsapp.configuration.security.SecurityUtils.JWT_ALGORITHM;

@RestController
@RequestMapping("/api")
public class AuthenticationResource {

    private final UserService userService;

    private final ReactiveAuthenticationManager authenticationManager;

    private final JwtEncoder jwtEncoder;

    public AuthenticationResource(UserService userService, ReactiveAuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> registerAccount(@Valid @RequestBody UserDTO newUser) {
        return userService.registerUser(newUser, newUser.getPassword()).then();
    }


    @PostMapping("/authenticate")
    public Mono<ResponseEntity<JWTTokenDTO>> authorize(@Valid @RequestBody Mono<LoginDTO> loginDTO) {
        return loginDTO
                .flatMap(
                        login ->
                                authenticationManager
                                        .authenticate(new UsernamePasswordAuthenticationToken(login.username(), login.password()))
                                        .flatMap(auth -> Mono.fromCallable(() -> this.createToken(auth)))
                )
                .map(jwt -> {
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setBearerAuth(jwt);
                    return new ResponseEntity<>(new JWTTokenDTO(jwt), httpHeaders, HttpStatus.OK);
                });
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity = now.plus(86400, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}

package jsc.com.authcodedemo.controller;

import jsc.com.authcodedemo.core.AuthenticationException;
import jsc.com.authcodedemo.service.KeycloakService;
import jsc.com.authcodedemo.service.dto.AuthRequestDto;
import jsc.com.authcodedemo.service.dto.TokenResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KeycloakService keycloakService;

    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    @PostMapping("/authenticate")
    public Mono<ResponseEntity<TokenResponseDto>> authenticate(@RequestBody AuthRequestDto request) {
        return keycloakService.getToken(request.getUsername(), request.getPassword())
                .map(ResponseEntity::ok)
                .onErrorMap(e -> {
                    // Log the error
                    System.err.println("Authentication error: " + e.getMessage());

                    // Transform any error into our custom AuthenticationException
                    return new AuthenticationException("Authentication failed: Invalid credentials or client configuration", e);
                });
    }

}

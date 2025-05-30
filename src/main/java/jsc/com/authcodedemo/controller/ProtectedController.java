package jsc.com.authcodedemo.controller;

import jsc.com.authcodedemo.service.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(new HealthResponse("Your Token is correct and verified."));
    }

}

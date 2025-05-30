package jsc.com.authcodedemo.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.token-endpoint}")
    private String tokenEndpoint;

    @GetMapping("/keycloak")
    public ResponseEntity<Map<String, String>> getKeycloakConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("authServerUrl", authServerUrl);
        config.put("realm", realm);
        config.put("clientId", clientId);
        config.put("tokenEndpoint", tokenEndpoint);

        return ResponseEntity.ok(config);
    }

}

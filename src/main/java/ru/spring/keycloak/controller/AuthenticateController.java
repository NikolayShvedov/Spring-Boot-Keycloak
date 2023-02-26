package ru.spring.keycloak.controller;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.spring.keycloak.service.KeyCloakService;
import ru.spring.keycloak.dto.AuthRequestDto;


@RequestMapping("/api")
@RestController
public class AuthenticateController {

    private final KeyCloakService keyCloakService;

    public AuthenticateController(KeyCloakService keyCloakService) {
        this.keyCloakService = keyCloakService;
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity handleAuthNotFoundException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AccessTokenResponse> authenticate(@RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(keyCloakService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@RequestHeader("refresh-token") String refreshToken) {
        return ResponseEntity.ok(keyCloakService.refreshToken(refreshToken));
    }

    @PostMapping("/user/info")
    public ResponseEntity<UserInfo> userInfo(@RequestHeader("access-token") String accessToken) {
        return ResponseEntity.ok(keyCloakService.getUserInfo(accessToken));
    }
}

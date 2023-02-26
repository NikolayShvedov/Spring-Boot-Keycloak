package ru.spring.keycloak.service;

import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import ru.spring.keycloak.config.KeycloakConfiguration;
import ru.spring.keycloak.dto.AuthRequestDto;

@Validated
@Component
public class KeyCloakService {

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${api.keycloak.authorization-uri}")
    private String authorizationURI;

    @Value("${api.keycloak.user-info-uri}")
    private String userInfoURI;

    private final KeycloakConfiguration keycloakConfiguration;

    public KeyCloakService(KeycloakConfiguration keycloakConfiguration) {
        this.keycloakConfiguration = keycloakConfiguration;
    }

    public AccessTokenResponse authenticate(AuthRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("username", request.getUsername());
        parameters.add("password", request.getPassword());
        parameters.add("grant_type", "password");
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
        AccessTokenResponse accessTokenResponse = keycloakConfiguration.getRestTemplate().exchange(
                authorizationURI,
                HttpMethod.POST,
                entity,
                AccessTokenResponse.class
        ).getBody();
        return accessTokenResponse;
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);
        parameters.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
        AccessTokenResponse accessTokenResponse = keycloakConfiguration.getRestTemplate().exchange(authorizationURI,
                HttpMethod.POST,
                entity,
                AccessTokenResponse.class
        ).getBody();
        return accessTokenResponse;
    }

    public UserInfo getUserInfo(String accessToken) {
        UserInfo userInfo = keycloakConfiguration.getRestTemplate().exchange(
                userInfoURI,
                HttpMethod.GET,
                new HttpEntity<>(null, new HttpHeaders() {{
                    setBearerAuth(accessToken);
                }}),
                UserInfo.class).getBody();
        return userInfo;
    }
}




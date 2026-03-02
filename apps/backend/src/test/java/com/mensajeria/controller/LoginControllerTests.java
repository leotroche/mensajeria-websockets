package com.mensajeria.controller;

import com.mensajeria.controller.dto.security.LoginData;
import com.mensajeria.controller.dto.security.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // importante poner en TODOS los tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LoginControllerTests {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void setup() throws Exception {
         webClient = WebClient.create("http://localhost:" + port);

    }

    @Test
    void loginWithInvalidCredentialsWillReturnError() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("999999999999999999");

        WebClientResponseException exception = assertThrows(
                WebClientResponseException.class,
                () -> webClient.post()
                        .uri("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(loginRequest)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block()
        );

        String body = exception.getResponseBodyAsString();
        JsonNode root = new ObjectMapper().readTree(body);

        assertEquals("User or password is not valid.", root.get("message").asText());
    }

    @Test
    void loginWillGiveToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        String fetchResponse = webClient.post()
                .uri("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(fetchResponse);
        JsonNode dataNode = root.get("data");
        LoginData loginData = mapper.treeToValue(dataNode, LoginData.class);

        assertNotNull(loginData.token());
    }


}

package com.mensajeria.controller;

import com.mensajeria.controller.dto.UserInfo;
import com.mensajeria.controller.dto.security.LoginData;
import com.mensajeria.controller.dto.security.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test") // importante poner en TODOS los tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserInfoControllerTests {

    @LocalServerPort
    private int port;

    private WebClient webClient;
    private LoginData loginData;
    private String token;

    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void setup() throws Exception {
        webClient = WebClient.create("http://localhost:" + port);

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
        loginData = mapper.treeToValue(dataNode, LoginData.class);
        token = loginData.token();

    }

    @Test
    void userInfoWillGiveCorrectIdForUser() throws Exception {

        String fetchResponse = webClient.get()
                .uri("/api/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginData.token())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(fetchResponse);
        JsonNode dataNode = root.get("userId");

        assertEquals("1", dataNode.asString()); // TODO esto podría llegar a cambiar (por el id)
    }

    @Test
    void loginWillGiveCorrectUsernameForUser() throws Exception {
        String fetchResponse = webClient.get()
                .uri("/api/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginData.token())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(fetchResponse);
        JsonNode dataNode = root.get("username");

        assertEquals("pepe", dataNode.asString());
    }

}

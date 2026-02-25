package com.mensajeria.controller;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.Message;
import com.mensajeria.security.jwt.dto.LoginData;
import com.mensajeria.security.jwt.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;
import java.util.concurrent.*;

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
    void loginWillGiveCorrectIdForUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        String fetchResponse = webClient.post()
                .uri("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(fetchResponse);
        JsonNode dataNode = root.get("data");
        LoginData loginData = mapper.treeToValue(dataNode, LoginData.class);

        assertEquals("1", loginData.userId()); // TODO esto podría llegar a cambiar (por el id)
    }

    @Test
    void loginWillGiveCorrectUsernameForUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        String fetchResponse = webClient.post()
                .uri("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(fetchResponse);
        JsonNode dataNode = root.get("data");
        LoginData loginData = mapper.treeToValue(dataNode, LoginData.class);

        assertEquals("pepe", loginData.username());
    }

    @Test
    void loginWillGiveToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        String fetchResponse = webClient.post()
                .uri("/signin")
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

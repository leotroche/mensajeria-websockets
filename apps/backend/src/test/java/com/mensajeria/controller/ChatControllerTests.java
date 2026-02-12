package com.mensajeria.controller;

import com.mensajeria.model.websocket.Information;
import com.mensajeria.model.websocket.Message;
import com.mensajeria.security.jwt.dto.LoginRequest;
import com.mensajeria.security.jwt.dto.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatControllerTests {
    // TODO revisar que sockJS esté activo

    @LocalServerPort
    private int port;

    // STOMP
    private WebSocketStompClient stompClient;
    private LinkedBlockingQueue<Information> blockingQueue;
    private StompSession session;
    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void setup() throws Exception {
//        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
//        headers.add("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZXBlIiwiaWF0IjoxNzcwOTAzNTc1LCJleHAiOjE3NzA5MjM1NzV9.v2XuQMaBrPWCdfJxPWW-RWWP7iIG3pICUiBoz_MeiPQ");
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + "eyeyey");

        stompClient = new WebSocketStompClient(
                new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
        blockingQueue = new LinkedBlockingQueue<>();
        session = stompClient
                .connectAsync(
                        "ws://localhost:" + port + "/chats",
                        new StompSessionHandlerAdapter() {},
                        stompHeaders
                )
                .get(1, TimeUnit.SECONDS);

        StompFrameHandler handler = getStompFrameHandler(blockingQueue);

        session.subscribe("/topic/canal1", handler);
    }

    @Test
    void shouldSendAndReceiveMessage() throws Exception {
        session.send("/app/chat1", new Message("anonymous", "hola fruta"));

        Information response = blockingQueue.poll(10, TimeUnit.SECONDS);

        assertEquals("hola fruta", response.text());
    }

    @Test
    void sendMessageOnKeyAndReceiveCorrectID() throws Exception {

        WebClient webClient = WebClient.create("http://localhost:" + port); // TODO pasar a .env

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        String fetchResponse = webClient.post()
                .uri("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // makes it synchronous

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(fetchResponse);
        String username = root.get("username").asString();

        session.send("/app/chat1", new Message(username, "hola fruta"));

        Information response = blockingQueue.poll(10, TimeUnit.SECONDS);

        assertEquals("hola fruta", response.text());
        assertEquals("1", response.id());
    }

    private static StompFrameHandler getStompFrameHandler(BlockingQueue<Information> blockingQueue) {
        StompFrameHandler handler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Information.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((Information) payload);
            }
        };
        return handler;
    }
}

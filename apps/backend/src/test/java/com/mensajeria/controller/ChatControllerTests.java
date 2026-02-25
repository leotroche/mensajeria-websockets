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
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.stomp.*;
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
public class ChatControllerTests {

    @LocalServerPort
    private int port;

    // STOMP
    private WebSocketStompClient stompClient;
    private LinkedBlockingQueue<Information> blockingQueue;
    private StompSession session;

    private StompHeaders validStompHeadersForSubscribe;
    private StompHeaders validStompHeadersForSend;

    private String token;
    StompFrameHandler handler;

    private StompHeaders invalidStompHeaders;


    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void setup() throws Exception {

//        Thread.sleep(5000);

        WebClient webClient = WebClient.create("http://localhost:" + port);
//        String csrfResponse = webClient.get() // TODO analizar CSRF
//                .uri("/csrf")
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        token = getToken(webClient, loginRequest);

        validStompHeadersForSubscribe = getStompHeadersForSubscribe(token);
        validStompHeadersForSend = getStompHeadersForSend(token);

        connectToChat();

        handler = getStompFrameHandler(blockingQueue);

    }

    private void connectToChat() throws InterruptedException, ExecutionException, TimeoutException {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
        blockingQueue = new LinkedBlockingQueue<>();

        session = stompClient
                .connectAsync(
                        "ws://localhost:" + port + "/chats", // TODO pasar a .env
                        new StompSessionHandlerAdapter() {}
                        // no andan los headers en esta librería
                )
                .get(1, TimeUnit.SECONDS);
    }

    private static StompHeaders getStompHeadersForSubscribe(String token) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/topic/canal1");
        stompHeaders.add("Authorization", "Bearer " + token);
        return stompHeaders;
    }

    private static StompHeaders getStompHeadersForSend(String token) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/chat1");
        stompHeaders.add("Authorization", "Bearer " + token);
        return stompHeaders;
    }

    private static String getToken(WebClient webClient, LoginRequest loginRequest) {
        String fetchResponse = webClient.post()
                .uri("/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();  // makes it synchronous

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(fetchResponse);
        JsonNode dataNode = root.get("data");
        LoginData loginData = mapper.treeToValue(dataNode, LoginData.class);

        return loginData.token();
    }

    @Test
    void shouldSendAndReceiveMessage() throws Exception {

        session.subscribe(validStompHeadersForSubscribe, handler);

        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertEquals("hola fruta", response.text());
    }

    @Test
    void sendMessageOnKeyAndReceiveCorrectID() throws Exception {

        session.subscribe(validStompHeadersForSubscribe, handler);

        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertEquals("hola fruta", response.text());
        assertEquals("1", response.userId());
    }

    @Test
    void invalidTokenCantSubscribe() throws Exception {

        invalidStompHeaders = getStompHeadersForSubscribe("123456");

        LinkedBlockingQueue<StompHeaders> messageQueue = new LinkedBlockingQueue<>();

        session.subscribe(invalidStompHeaders, handler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        StompHeaders response = messageQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void emptyTokenCantSubscribe() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe("");

        session.subscribe(invalidStompHeaders, handler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSubscribeWithExtra() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe(token + "3");

        session.subscribe(invalidStompHeaders, handler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSubscribeWithOneLess() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe(token.substring(0,1));

        session.subscribe(invalidStompHeaders, handler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }


    @Test
    void invalidTokenCantSendMessage() throws Exception {

        invalidStompHeaders = getStompHeadersForSend("123456");

        LinkedBlockingQueue<StompHeaders> messageQueue = new LinkedBlockingQueue<>();

        session.subscribe(validStompHeadersForSubscribe, handler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        StompHeaders response = messageQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void emptyTokenCantSendMessage() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend("");

        session.subscribe(validStompHeadersForSubscribe, handler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSendMessageWithExtra() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend(token + "3");

        session.subscribe(validStompHeadersForSubscribe, handler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSendMessageWithOneLess() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend(token.substring(0,1));

        session.subscribe(validStompHeadersForSubscribe, handler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        Information response = blockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
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

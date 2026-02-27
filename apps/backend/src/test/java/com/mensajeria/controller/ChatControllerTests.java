package com.mensajeria.controller;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.Message;
import com.mensajeria.controller.dto.security.LoginData;
import com.mensajeria.controller.dto.security.LoginRequest;
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
    private LinkedBlockingQueue<Information> pepeBlockingQueue;
    private StompSession session;

    private StompHeaders validStompHeadersForSubscribe;
    private StompHeaders validStompHeadersForSend;

    private String pepeToken;
    private StompFrameHandler pepeHandler;

    private StompHeaders invalidStompHeaders;

    private String pepaToken;
    private StompHeaders pepaStompHeadersSubscribe;
    private StompHeaders pepaStompHeadersSend;
    private LinkedBlockingQueue<Information> pepaBlockingQueue;
    private StompFrameHandler pepaHandler;


    @Autowired
    private JsonMapper jsonMapper;

    @BeforeEach
    void setup() throws Exception {

        WebClient webClient = WebClient.create("http://localhost:" + port);

        initializePepe(webClient);

        initializePepa(webClient);

    }

    private void initializePepe(WebClient webClient) throws InterruptedException, ExecutionException, TimeoutException {
        pepeBlockingQueue = new LinkedBlockingQueue<>();
        LoginRequest pepeLoginRequest = new LoginRequest();
        pepeLoginRequest.setUsername("pepe");
        pepeLoginRequest.setPassword("pepe1234");

        pepeToken = getToken(webClient, pepeLoginRequest);

        validStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken);
        validStompHeadersForSend = getStompHeadersForSend(pepeToken);

        connectToChat();

        pepeHandler = getStompFrameHandler(pepeBlockingQueue);
    }

    private void initializePepa(WebClient webClient) throws InterruptedException, ExecutionException, TimeoutException {
        pepaBlockingQueue = new LinkedBlockingQueue<>();
        LoginRequest pepaLoginRequest = new LoginRequest();
        pepaLoginRequest.setUsername("pepa");
        pepaLoginRequest.setPassword("1234pepe");

        pepaToken = getToken(webClient, pepaLoginRequest);

        pepaStompHeadersSubscribe = getStompHeadersForSubscribe(pepaToken);
        pepaStompHeadersSend = getStompHeadersForSend(pepaToken);

        connectToChat();

        pepaHandler = getStompFrameHandler(pepaBlockingQueue);
    }

    private void connectToChat()
            throws InterruptedException, ExecutionException, TimeoutException {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

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
                .uri("/api/login")
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
    void sendMessageFromIdGetsThatIdFromUser2() throws Exception {

        Runnable sendPepaMessage = () -> {
            // se crea en un thread aparte para chequear
            session.subscribe(pepaStompHeadersSubscribe, pepaHandler);
            session.send(pepaStompHeadersSend, new Message("hola pepe"));
        };

        session.subscribe(validStompHeadersForSubscribe, pepeHandler);

        new Thread(sendPepaMessage).start();

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertEquals("hola pepe", response.text());
        assertEquals("2", response.userId());
    }

    @Test
    void sendMessageFromIdGetsThatIdFromUser1() throws Exception {

        Runnable sendPepeMessage = () -> {
            // se crea en un thread aparte para chequear
            session.subscribe(validStompHeadersForSubscribe, pepeHandler);
            session.send(validStompHeadersForSend, new Message("hola pepa"));
        };

        session.subscribe(pepaStompHeadersSubscribe, pepaHandler);

        new Thread(sendPepeMessage).start();

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertEquals("hola pepa", response.text());
        assertEquals("1", response.userId());
    }



    @Test
    void shouldSendAndReceiveMessage() throws Exception {

        session.subscribe(validStompHeadersForSubscribe, pepeHandler);

        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertEquals("hola fruta", response.text());
    }

    @Test
    void sendMessageOnKeyAndReceiveCorrectID() throws Exception {

        session.subscribe(validStompHeadersForSubscribe, pepeHandler);

        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertEquals("hola fruta", response.text());
        assertEquals("1", response.userId());
    }

    @Test
    void invalidTokenCantSubscribe() throws Exception {

        invalidStompHeaders = getStompHeadersForSubscribe("123456");

        LinkedBlockingQueue<StompHeaders> messageQueue = new LinkedBlockingQueue<>();

        session.subscribe(invalidStompHeaders, pepeHandler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        StompHeaders response = messageQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void emptyTokenCantSubscribe() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe("");

        session.subscribe(invalidStompHeaders, pepeHandler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSubscribeWithExtra() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe(pepeToken + "3");

        session.subscribe(invalidStompHeaders, pepeHandler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSubscribeWithOneLess() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe(pepeToken.substring(0,1));

        session.subscribe(invalidStompHeaders, pepeHandler);
        session.send(validStompHeadersForSend, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }


    @Test
    void invalidTokenCantSendMessage() throws Exception {

        invalidStompHeaders = getStompHeadersForSend("123456");

        LinkedBlockingQueue<StompHeaders> messageQueue = new LinkedBlockingQueue<>();

        session.subscribe(validStompHeadersForSubscribe, pepeHandler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        StompHeaders response = messageQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void emptyTokenCantSendMessage() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend("");

        session.subscribe(validStompHeadersForSubscribe, pepeHandler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSendMessageWithExtra() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend(pepeToken + "3");

        session.subscribe(validStompHeadersForSubscribe, pepeHandler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSendMessageWithOneLess() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend(pepeToken.substring(0,1));

        session.subscribe(validStompHeadersForSubscribe, pepeHandler);
        session.send(invalidStompHeaders, new Message("hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    private static StompFrameHandler getStompFrameHandler(BlockingQueue<Information> pepeBlockingQueue) {
        StompFrameHandler handler = new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Information.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                pepeBlockingQueue.add((Information) payload);
            }
        };
        return handler;
    }
}

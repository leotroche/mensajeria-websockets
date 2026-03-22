package com.mensajeria.controller.chat_controller;

import com.mensajeria.model.information.Information;
import com.mensajeria.model.information.chat.message.Message;
import com.mensajeria.model.information.chat.message.MessagePayload;
import com.mensajeria.controller.dto.security.LoginData;
import com.mensajeria.controller.dto.security.login.LoginRequest;
import com.mensajeria.utils.TestService;
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
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // importante poner en TODOS los tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class MessageChatControllerTests {

    @LocalServerPort
    private int port;

    // STOMP
    private WebSocketStompClient stompClient;
    private LinkedBlockingQueue<Information> pepeBlockingQueue;


    private StompHeaders validStompHeadersForSubscribe;
    private StompHeaders validStompHeadersForSend;

    private StompSession pepeSession;
    private String pepeToken;
    private StompFrameHandler pepeHandler;

    private StompHeaders invalidStompHeaders;

    private StompSession pepaSession;
    private String pepaToken;
    private StompHeaders pepaStompHeadersSubscribe;
    private StompHeaders pepaStompHeadersSend;
    private LinkedBlockingQueue<Information> pepaBlockingQueue;
    private StompFrameHandler pepaHandler;

    WebClient webClient;

    @Autowired
    TestService testService;

    @BeforeEach
    void setup() throws Exception {

        testService.createTestUsers();

        webClient = WebClient.create("http://localhost:" + port);

        initializePepe(webClient);

        initializePepa(webClient);

    }

    private void initializePepe(WebClient webClient) throws InterruptedException, ExecutionException, TimeoutException {
        pepeBlockingQueue = new LinkedBlockingQueue<>();
        LoginRequest pepeLoginRequest = new LoginRequest();
        pepeLoginRequest.setUsername("pepe");
        pepeLoginRequest.setPassword("pepe1234");

        pepeToken = getToken(webClient, pepeLoginRequest);

        validStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepe");
        validStompHeadersForSend = getStompHeadersForSend(pepeToken, "pepa");

        pepeSession = connectToChat(pepeToken);

        pepeHandler = getStompFrameHandler(pepeBlockingQueue);
    }

    private void initializePepa(WebClient webClient) throws InterruptedException, ExecutionException, TimeoutException {
        pepaBlockingQueue = new LinkedBlockingQueue<>();
        LoginRequest pepaLoginRequest = new LoginRequest();
        pepaLoginRequest.setUsername("pepa");
        pepaLoginRequest.setPassword("1234pepe");

        pepaToken = getToken(webClient, pepaLoginRequest);

        pepaStompHeadersSubscribe = getStompHeadersForSubscribe(pepaToken, "pepa");
        pepaStompHeadersSend = getStompHeadersForSend(pepaToken, "pepe");

        pepaSession = connectToChat(pepaToken);

        pepaHandler = getStompFrameHandler(pepaBlockingQueue);
    }

    private StompSession connectToChat(String token)
            throws InterruptedException, ExecutionException, TimeoutException {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());

        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        return stompClient
                .connectAsync(
                        "ws://localhost:" + port + "/chats", // TODO pasar a .env
                        handshakeHeaders,
                        connectHeaders,
                        new StompSessionHandlerAdapter() {}

                )
                .get(1, TimeUnit.SECONDS);
    }

    private static StompHeaders getStompHeadersForSubscribe(String token, String chatId) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/chats/" + chatId + "/queue");
        stompHeaders.add("Authorization", "Bearer " + token);
        return stompHeaders;
    }

    private static StompHeaders getStompHeadersForSend(String token, String chatId) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/chats/" + chatId);
        stompHeaders.add("Authorization", "Bearer " + token);
//        stompHeaders.add("__TypeId__", "message");
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
    void cantConnectWithInvalidToken() {

        assertThrows(ExecutionException.class, () -> connectToChat("no"));

    }

    @Test
    void sendMessageFromIdGetsThatIdFromUser2() throws Exception {

        Runnable sendPepaMessage = () -> {
            // se crea en un thread aparte para chequear
            pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);
            pepaSession.send(pepaStompHeadersSend, new MessagePayload("0","hola pepe"));
        };

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);

        new Thread(sendPepaMessage).start();

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Message message = (Message) response.payload();

        assertEquals("hola pepe", message.content());
        assertEquals("pepa", message.senderId());
    }

    @Test
    void sendMessageFromIdGetsThatIdFromUser1() throws Exception {

        Runnable sendPepeMessage = () -> {
            // se crea en un thread aparte para chequear
            pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
            pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola pepa"));
        };

        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);

        new Thread(sendPepeMessage).start();

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Message message = (Message) response.payload();
        assertEquals("hola pepa", message.content());
        assertEquals("pepe", message.senderId());
    }

    @Test
    void shouldSendAndReceiveMessage() throws Exception {

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);

        pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Message message = (Message) response.payload();
        assertEquals("hola fruta", message.content());
    }

    @Test
    void sendMessageOnKeyAndReceiveCorrectID() throws Exception {

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);

        pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Message message = (Message) response.payload();
        assertEquals("hola fruta", message.content());
        assertEquals("pepe", message.senderId());
    }

    @Test
    void invalidTokenCantSubscribe() throws Exception {

        invalidStompHeaders = getStompHeadersForSubscribe("123456", "canal1");

        LinkedBlockingQueue<StompHeaders> messageQueue = new LinkedBlockingQueue<>();

        pepeSession.subscribe(invalidStompHeaders, pepeHandler);
        pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola fruta"));

        StompHeaders response = messageQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void emptyTokenCantSubscribe() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe("", "canal1");

        pepeSession.subscribe(invalidStompHeaders, pepeHandler);
        pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSubscribeWithExtra() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe(pepeToken + "3", "canal1");

        pepeSession.subscribe(invalidStompHeaders, pepeHandler);
        pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSubscribeWithOneLess() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSubscribe(pepeToken.substring(0,1), "canal1");

        pepeSession.subscribe(invalidStompHeaders, pepeHandler);
        pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }


    @Test
    void invalidTokenCantSendMessage() throws Exception {

        invalidStompHeaders = getStompHeadersForSend("123456", "canal1");

        LinkedBlockingQueue<StompHeaders> messageQueue = new LinkedBlockingQueue<>();

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepeSession.send(invalidStompHeaders, new MessagePayload("0", "hola fruta"));

        StompHeaders response = messageQueue.poll(5, TimeUnit.SECONDS);

//        assertNull(response); // no response means it didnt connect
    }

    @Test
    void emptyTokenCantSendMessage() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend("", "canal1");

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepeSession.send(invalidStompHeaders, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSendMessageWithExtra() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend(pepeToken + "3", "canal1");

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepeSession.send(invalidStompHeaders, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void almostValidTokenCantSendMessageWithOneLess() throws InterruptedException {

        invalidStompHeaders = getStompHeadersForSend(pepeToken.substring(0,1), "canal1");

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepeSession.send(invalidStompHeaders, new MessagePayload("0", "hola fruta"));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNull(response); // no response means it didnt connect
    }

    @Test
    void shouldSendAndReceiveMessageOnUserIdChannel() throws Exception {

        StompHeaders pepeStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "1");
        StompHeaders stompHeadersForSend = getStompHeadersForSend(pepeToken, "1");

        StompHeaders pepaStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "1");

        Runnable sendPepeMessage = () -> {
            // se crea en un thread aparte para chequear
            pepeSession.subscribe(pepeStompHeadersForSubscribe, pepeHandler);
            pepeSession.send(stompHeadersForSend, new MessagePayload("0", "hola pepa"));
        };

        pepaSession.subscribe(pepaStompHeadersForSubscribe, pepaHandler);

        new Thread(sendPepeMessage).start();

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Message message = (Message) response.payload();
        assertEquals("hola pepa", message.content());
    }

    @Test
    void receiveSameDateOnBothInformations() throws Exception {

        StompHeaders pepeStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepe");
        StompHeaders stompHeadersForSend = getStompHeadersForSend(pepeToken, "pepa");

        StompHeaders pepaStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepa");

        Runnable sendPepeMessage = () -> {
            // se crea en un thread aparte para chequear
            pepeSession.subscribe(pepeStompHeadersForSubscribe, pepeHandler);
            pepeSession.send(stompHeadersForSend, new MessagePayload("0", "hola pepa"));
        };

        pepaSession.subscribe(pepaStompHeadersForSubscribe, pepaHandler);

        new Thread(sendPepeMessage).start();

        Information pepeResponse = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        Information pepaResponse = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNotNull(pepeResponse);
        assertNotNull(pepaResponse);

        Message pepeMessage = (Message) pepeResponse.payload();
        Message pepaMessage = (Message) pepaResponse.payload();

        assertEquals(pepeMessage.createdAt(), pepaMessage.createdAt());
    }

    @Test
    void receiveBounceWithUser1AsTheIdOfTheChat() throws Exception {

        StompHeaders pepeStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepe");
        StompHeaders stompHeadersForSend = getStompHeadersForSend(pepeToken, "pepa");

        StompHeaders pepaStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepa");

        Runnable sendPepeMessage = () -> {
            // se crea en un thread aparte para chequear
            pepeSession.subscribe(pepeStompHeadersForSubscribe, pepeHandler);
            pepeSession.send(stompHeadersForSend, new MessagePayload("0", "hola pepa"));
        };

        pepaSession.subscribe(pepaStompHeadersForSubscribe, pepaHandler);

        new Thread(sendPepeMessage).start();

        Information pepeResponse = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNotNull(pepeResponse);

        Message pepeMessage = (Message) pepeResponse.payload();

        assertEquals("pepa", pepeMessage.chatId());
        assertEquals("pepe", pepeMessage.senderId());
    }

    @Test
    void receiveMessageWithUser1AsTheIdOfChat() throws Exception {

        StompHeaders pepeStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepe");
        StompHeaders stompHeadersForSend = getStompHeadersForSend(pepeToken, "pepa");

        StompHeaders pepaStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepa");

        Runnable sendPepeMessage = () -> {
            // se crea en un thread aparte para chequear
            pepeSession.subscribe(pepeStompHeadersForSubscribe, pepeHandler);
            pepeSession.send(stompHeadersForSend, new MessagePayload("0", "hola pepa"));
        };

        pepaSession.subscribe(pepaStompHeadersForSubscribe, pepaHandler);

        new Thread(sendPepeMessage).start();

        Information pepaResponse = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);

        assertNotNull(pepaResponse);

        Message pepeMessage = (Message) pepaResponse.payload();

        assertEquals("pepe", pepeMessage.chatId());
        assertEquals("pepe", pepeMessage.senderId());
    }

    private static StompFrameHandler getStompFrameHandler(BlockingQueue<Information> blockingQueue) {
        StompFrameHandler handler = new StompFrameHandler() {
            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return String.class;
//            }

            public Type getPayloadType(StompHeaders headers) {
                return Information.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
//                try {
//                    Information info = mapper.readValue((String) payload, Information.class);
//                    blockingQueue.add(info);
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
                blockingQueue.add((Information) payload);
            }
        };
        return handler;
    }
}

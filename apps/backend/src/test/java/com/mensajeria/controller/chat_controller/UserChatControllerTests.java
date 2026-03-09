//package com.mensajeria.controller.chat_controller;
//
//import com.mensajeria.model.chat.Information;
//import com.mensajeria.model.chat.MessagePayload;
//import com.mensajeria.controller.dto.security.LoginData;
//import com.mensajeria.controller.dto.security.login.LoginRequest;
//import com.mensajeria.utils.TestService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.MediaType;
//import org.springframework.messaging.converter.*;
//import org.springframework.messaging.simp.stomp.*;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.socket.WebSocketHttpHeaders;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import tools.jackson.databind.JsonNode;
//import tools.jackson.databind.ObjectMapper;
//import tools.jackson.databind.json.JsonMapper;
//
//import java.lang.reflect.Type;
//import java.util.concurrent.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ActiveProfiles("test") // importante poner en TODOS los tests
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//public class UserChatControllerTests {
//
//    @LocalServerPort
//    private int port;
//
//    // STOMP
//    private WebSocketStompClient stompClient;
//    private LinkedBlockingQueue<Information> pepeBlockingQueue;
//
//    private StompSession pepeSession;
//    private StompSession pepaSession;
//
//    private StompHeaders validStompHeadersForSubscribe;
//    private StompHeaders validStompHeadersForSend;
//
//    private String pepeToken;
//    private StompFrameHandler pepeHandler;
//
//    private StompHeaders invalidStompHeaders;
//
//    private String pepaToken;
//    private StompHeaders pepaStompHeadersSubscribe;
//    private StompHeaders pepaStompHeadersSend;
//    private LinkedBlockingQueue<Information> pepaBlockingQueue;
//    private StompFrameHandler pepaHandler;
//
//    @Autowired
//    TestService testService;
//
//    @Autowired
//    private JsonMapper jsonMapper;
//
//    @BeforeEach
//    void setup() throws Exception {
//
//        testService.createTestUsers();
//
//        WebClient webClient = WebClient.create("http://localhost:" + port);
//
//        initializePepe(webClient);
//
//        initializePepa(webClient);
//
//    }
//
//    private void initializePepe(WebClient webClient) throws InterruptedException, ExecutionException, TimeoutException {
//        pepeBlockingQueue = new LinkedBlockingQueue<>();
//        LoginRequest pepeLoginRequest = new LoginRequest();
//        pepeLoginRequest.setUsername("pepe");
//        pepeLoginRequest.setPassword("pepe1234");
//
//        pepeToken = getToken(webClient, pepeLoginRequest);
//
//        validStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepe");
//        validStompHeadersForSend = getStompHeadersForSend(pepeToken, "pepa");
//
//        pepeSession = connectToChat(pepeToken);
//
//        pepeHandler = getStompFrameHandler(pepeBlockingQueue);
//    }
//
//    private void initializePepa(WebClient webClient) throws InterruptedException, ExecutionException, TimeoutException {
//        pepaBlockingQueue = new LinkedBlockingQueue<>();
//        LoginRequest pepaLoginRequest = new LoginRequest();
//        pepaLoginRequest.setUsername("pepa");
//        pepaLoginRequest.setPassword("1234pepe");
//
//        pepaToken = getToken(webClient, pepaLoginRequest);
//
//        pepaStompHeadersSubscribe = getStompHeadersForSubscribe(pepaToken, "pepa");
//        pepaStompHeadersSend = getStompHeadersForSend(pepaToken, "pepe");
//
//        pepaSession = connectToChat(pepaToken);
//
//        pepaHandler = getStompFrameHandler(pepaBlockingQueue);
//    }
//
//    private StompSession connectToChat(String token)
//            throws InterruptedException, ExecutionException, TimeoutException {
//        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
//        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
//        StompHeaders connectHeaders = new StompHeaders();
//        connectHeaders.add("Authorization", "Bearer " + token);
//
//        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
////        handshakeHeaders.add("Authorization", "Bearer " + token);
//
//        return stompClient
//                .connectAsync(
//                        "ws://localhost:" + port + "/chats", // TODO pasar a .env
//                        handshakeHeaders,
//                        connectHeaders,
//                        new StompSessionHandlerAdapter() {}
//                        // no andan los headers en esta librería
//                )
//                .get(1, TimeUnit.SECONDS);
//    }
//
//    private static StompHeaders getStompHeadersForSubscribe(String token, String subscriberName) {
//        StompHeaders stompHeaders = new StompHeaders();
//        stompHeaders.setDestination("/user/" + subscriberName + "/queue/messages");
//        stompHeaders.add("Authorization", "Bearer " + token);
//        return stompHeaders;
//    }
//
//    private static StompHeaders getStompHeadersForSend(String token, String receiverName) {
//        StompHeaders stompHeaders = new StompHeaders();
//        stompHeaders.setDestination("/app/chat/" + receiverName);
//        stompHeaders.add("Authorization", "Bearer " + token);
//        return stompHeaders;
//    }
//
//    private static String getToken(WebClient webClient, LoginRequest loginRequest) {
//        String fetchResponse = webClient.post()
//                .uri("/api/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(loginRequest)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();  // makes it synchronous
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(fetchResponse);
//        JsonNode dataNode = root.get("data");
//        LoginData loginData = mapper.treeToValue(dataNode, LoginData.class);
//
//        return loginData.token();
//    }
//
//    @Test
//    void sendMessageFromIdGetsThatIdFromUser2() throws Exception {
//
//        Runnable sendPepaMessage = () -> {
//            // se crea en un thread aparte para chequear
//            pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);
//            pepaSession.send(pepaStompHeadersSend, new MessagePayload("0","hola pepe"));
//        };
//
//        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
//        Thread.sleep(200);
//
//        new Thread(sendPepaMessage).start();
//
//        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
//
//        assertNotNull(response);
//        assertEquals("hola pepe", response.content());
//        assertEquals("pepa", response.senderId());
//    }
//
//
//    @Test
//    void sendMessageFromIdGetsThatIdFromUser1() throws Exception {
//
//        Runnable sendPepeMessage = () -> {
//            // se crea en un thread aparte para chequear
//            pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
//            pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola pepa"));
//        };
//
//        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);
//        Thread.sleep(200);
//
//        new Thread(sendPepeMessage).start();
//
//        Information response = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);
//
//        assertNotNull(response);
//        assertEquals("hola pepa", response.content());
//        assertEquals("pepe", response.senderId());
//    }
//
//    @Test
//    void sendMessageToUserGivesMessageToSenderAsWell() throws Exception {
//
//        Runnable sendPepeMessage = () -> {
//            // se crea en un thread aparte para chequear
//            pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
//            pepeSession.send(validStompHeadersForSend, new MessagePayload("0", "hola pepa"));
//        };
//
//        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);
//        Thread.sleep(200);
//
//        new Thread(sendPepeMessage).start();
//
//        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
//
//        assertNotNull(response);
//        assertEquals("hola pepa", response.content());
//        assertEquals("pepe", response.senderId());
//    }
//
//
//
//
//
//
//
//    private static StompFrameHandler getStompFrameHandler(BlockingQueue<Information> pepeBlockingQueue) {
//        StompFrameHandler handler = new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return Information.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                pepeBlockingQueue.add((Information) payload);
//            }
//        };
//        return handler;
//    }
//}

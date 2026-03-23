package com.mensajeria.controller.chat_controller;

import com.mensajeria.controller.dto.payload.RequestSendPayload;
import com.mensajeria.controller.dto.payload.data.RequestAcceptData;
import com.mensajeria.controller.dto.payload.data.RequestSendData;
import com.mensajeria.controller.dto.security.LoginData;
import com.mensajeria.controller.dto.security.login.LoginRequest;
import com.mensajeria.model.information.Information;
import com.mensajeria.controller.dto.payload.RequestAcceptPayload;
import com.mensajeria.model.information.chat.request.AcceptRequest;
import com.mensajeria.model.information.chat.Chat;
import com.mensajeria.model.information.chat.request.SendRequest;
import com.mensajeria.utils.TestService;
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
public class RequestControllerTests {

    @LocalServerPort
    private int port;

    // STOMP
    private WebSocketStompClient stompClient;
    private LinkedBlockingQueue<Information> pepeBlockingQueue;


    private StompHeaders validStompHeadersForSubscribe;

    private StompSession pepeSession;
    private String pepeToken;
    private StompFrameHandler pepeHandler;

    private StompHeaders invalidStompHeaders;

    private StompSession pepaSession;
    private String pepaToken;
    private StompHeaders pepaStompHeadersSubscribe;
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

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);

    }

    private void initializePepe(WebClient webClient) throws InterruptedException, ExecutionException, TimeoutException {
        pepeBlockingQueue = new LinkedBlockingQueue<>();
        LoginRequest pepeLoginRequest = new LoginRequest();
        pepeLoginRequest.setUsername("pepe");
        pepeLoginRequest.setPassword("pepe1234");

        pepeToken = getToken(webClient, pepeLoginRequest);

        validStompHeadersForSubscribe = getStompHeadersForSubscribe(pepeToken, "pepe");

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
                        "ws://localhost:" + port + "/chats",
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

    private static StompHeaders getStompHeadersForAccept(String token) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/requests/accept");
        stompHeaders.add("Authorization", "Bearer " + token);
        return stompHeaders;
    }

    private static StompHeaders getStompHeadersForSend(String token) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/requests/send");
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
    void cantConnectWithInvalidToken() {

        assertThrows(ExecutionException.class, () -> connectToChat("no"));

    }

    @Test
    void sendRequestAndReceiveTheSenderOfIt() throws Exception {

        StompHeaders sendHeaders = getStompHeadersForSend(pepeToken);

        RequestSendData data = new RequestSendData("UUID", "pepe", "pepe", 121424545L);
        RequestSendPayload requestSendPayload = new RequestSendPayload(data, "pepa");
        pepeSession.send(sendHeaders, requestSendPayload);

        Information response = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        SendRequest request = (SendRequest) response.payload();
        assertEquals("pepe", request.sender().senderName());
    }

    @Test
    void sendRequestAndReceiveTheIdOfIt() throws Exception {

        StompHeaders sendHeaders = getStompHeadersForSend(pepeToken);

        RequestSendData data = new RequestSendData("UUID", "pepe", "pepe", 121424545L);
        RequestSendPayload requestSendPayload = new RequestSendPayload(data, "pepa");
        pepeSession.send(sendHeaders, requestSendPayload);

        Information response = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        SendRequest request = (SendRequest) response.payload();
        assertEquals("UUID", request.id());
    }

    @Test
    void acceptRequestAndReceiveChat() throws Exception {

        StompHeaders sendHeaders = getStompHeadersForAccept(pepeToken);

        Chat chat = new Chat("pepe", "pepe", "pepe.png", 212164L, 4545L, 0, null);
        RequestAcceptData data = new RequestAcceptData(chat);
        RequestAcceptPayload requestAcceptPayload = new RequestAcceptPayload(data, "pepa");
        pepeSession.send(sendHeaders, requestAcceptPayload);

        Information response = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        AcceptRequest request = (AcceptRequest) response.payload();
        assertEquals("pepe", request.chat().id());
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

package com.mensajeria.controller.chat_controller;

import com.mensajeria.controller.dto.security.LoginData;
import com.mensajeria.controller.dto.security.login.LoginRequest;
import com.mensajeria.model.information.Information;
import com.mensajeria.model.information.chat.request.Request;
import com.mensajeria.model.information.chat.request.RequestPayload;
import com.mensajeria.model.information.chat.request.RequestStatus;
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
public class RequestChatControllerTests {

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

    private static StompHeaders getStompHeadersForSend(String token, String receiverName) {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/app/chats/requests/" + receiverName);
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

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);

        pepeSession.send(validStompHeadersForSend, new RequestPayload("0", "pepa", "pepe", RequestStatus.PENDING));

        Information response = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Request request = (Request) response.getPayload();
        assertEquals("pepe", request.senderId());
    }

    @Test
    void sendRequestAndReceiveTheIdOfIt() throws Exception {

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);

        pepeSession.send(validStompHeadersForSend, new RequestPayload("0", "pepa", "pepe", RequestStatus.PENDING));

        Information response = pepaBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Request request = (Request) response.getPayload();
        assertEquals("0", request.id());
    }

    @Test
    void approvedRequestSendsItsChatId() throws Exception {

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);

        pepeSession.send(pepaStompHeadersSend, new RequestPayload("0", "pepa", "pepa", RequestStatus.ACCEPTED));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Request request = (Request) response.getPayload();
        assertEquals("pepa", request.chatId());
    }

    @Test
    void rejectedRequestSendsNullAsChatId() throws Exception {

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);

        pepeSession.send(pepaStompHeadersSend, new RequestPayload("0", "pepa", "pepa", RequestStatus.REJECTED));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Request request = (Request) response.getPayload();
        assertNull(request.chatId());
    }

    @Test
    void pendingRequestSendsNullAsChatId() throws Exception {

        pepeSession.subscribe(validStompHeadersForSubscribe, pepeHandler);
        pepaSession.subscribe(pepaStompHeadersSubscribe, pepaHandler);

        pepeSession.send(pepaStompHeadersSend, new RequestPayload("0", "pepa", "pepa", RequestStatus.PENDING));

        Information response = pepeBlockingQueue.poll(5, TimeUnit.SECONDS);
        assertNotNull(response);

        Request request = (Request) response.getPayload();
        assertNull(request.chatId());
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

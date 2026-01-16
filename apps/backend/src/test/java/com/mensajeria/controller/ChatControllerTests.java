package com.mensajeria.controller;

import com.mensajeria.websocket.Information;
import com.mensajeria.websocket.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Import(ChatControllerTests.TestSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})

public class ChatControllerTests {
    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE) // or LOWEST_PRECEDENCE depending on your setup
        SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(
                new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient())))
        );
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());
    }

    @Test
    void shouldSendAndReceiveMessage() throws Exception {
        BlockingQueue<Information> blockingQueue = new LinkedBlockingQueue<>();

        StompSession session = stompClient
                .connectAsync(
                        "ws://localhost:" + port + "/chats",
                        new StompSessionHandlerAdapter() {}
                )
                .get(1, TimeUnit.SECONDS);

        StompFrameHandler handler = getStompFrameHandler(blockingQueue);

        session.subscribe("/topic/canal1", handler);

        session.send("/app/chat1", new Message("hola fruta"));

        Information response = blockingQueue.poll(10, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals("hola fruta", response.content());
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

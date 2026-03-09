package com.mensajeria.config.websocket;


import com.mensajeria.interceptor.ConnectHandshakeInterceptor;
import com.mensajeria.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${WEBSOCKETS_CORS_PATTERN}")
    private String corsPattern;

    private final JwtUtils jwtUtils;

    public WebSocketConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/conversation");
//        registry.setUserDestinationPrefix("/user");
        registry.setApplicationDestinationPrefixes("/app");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chats")
//                .addInterceptors(new ConnectHandshakeInterceptor(jwtUtils))
                .setAllowedOriginPatterns(corsPattern); // TODO en mobile NO hay CORS, pero si hacemos algo web hay que especificar
    }

}

package com.mensajeria.controller.interceptor;

import com.mensajeria.security.jwt.JwtUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;

public class SubscribeChannelInterceptor implements ChannelInterceptor {

    private JwtUtils jwtUtils;

    public SubscribeChannelInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (!StompCommand.SUBSCRIBE.equals(accessor.getCommand())) return message;

        checkAuthorizationHeader(accessor);
        return message;
    }

    private void checkAuthorizationHeader(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");

        if (!isFormattedBearerToken(token)) throw new IllegalArgumentException("Invalid token");

        token = token.substring(7);

        if (!jwtUtils.validateJwtToken(token)) throw new IllegalArgumentException("Invalid token");

        Authentication auth = jwtUtils.getAuthentication(token);
        accessor.setUser(auth);
    }

    private static boolean isFormattedBearerToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }
}

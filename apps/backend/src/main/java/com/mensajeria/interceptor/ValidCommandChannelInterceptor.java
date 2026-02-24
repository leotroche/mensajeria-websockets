package com.mensajeria.interceptor;

import com.mensajeria.utils.JwtUtils;

import com.mensajeria.utils.ProfileValidator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;


public class ValidCommandChannelInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;
    private final ProfileValidator profileValidator = new ProfileValidator();

    public ValidCommandChannelInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (    !(
                    isValidConnect(accessor) || // TODO quitar el profileValidator
                    isCommand(StompCommand.SUBSCRIBE, accessor) ||
                    isCommand(StompCommand.SEND, accessor)
                )
        ) return message;

        checkAuthorizationHeader(accessor);
        return message;
    }

    private boolean isValidConnect(StompHeaderAccessor accessor) {
        return isCommand(StompCommand.CONNECT, accessor) && profileValidator.isTestProfileActive();
    }

    private static boolean isCommand(StompCommand command, StompHeaderAccessor accessor) {
        return command.equals(accessor.getCommand());
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


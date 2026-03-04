package com.mensajeria.interceptor;

import com.mensajeria.security.exception.InvalidTokenException;
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
                    isCommand(StompCommand.CONNECT, accessor) || // TODO revisar si vale la pena otros comandos por seguridad
                    isCommand(StompCommand.SUBSCRIBE, accessor) ||
                    isCommand(StompCommand.SEND, accessor)
                )
        ) return message;

        Authentication auth = jwtUtils.checkAuthorizationHeader(accessor);
        logUser(accessor, auth);
        return message;
    }

    private static boolean isCommand(StompCommand command, StompHeaderAccessor accessor) {
        return command.equals(accessor.getCommand());
    }

//    private void checkAuthorizationHeader(StompHeaderAccessor accessor) {
//
//        String token = accessor.getFirstNativeHeader("Authorization");
//
//        if (!isFormattedBearerToken(token)) throw new InvalidTokenException("Invalid token.");
//
//        token = token.substring(7);
//
//        if (!jwtUtils.validateJwtToken(token)) throw new InvalidTokenException("Invalid token.");
//
//        Authentication auth = jwtUtils.getAuthentication(token);
//        
//    }

    private static void logUser(StompHeaderAccessor accessor, Authentication auth) {
        if (accessor.getUser() != null) return; // si está logueado, no lo loguees de nuevo
        accessor.setUser(auth);
    }

    private static boolean isFormattedBearerToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }

}


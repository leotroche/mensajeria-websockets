package com.mensajeria.config.websocket;


import com.mensajeria.interceptor.ValidCommandChannelInterceptor;
import com.mensajeria.utils.JwtUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final ApplicationContext applicationContext;

    private final AuthorizationManager<Message<?>> authorizationManager;

    private final JwtUtils jwtUtils;


    public WebSocketSecurityConfig(ApplicationContext applicationContext, AuthorizationManager<Message<?>> authorizationManager, JwtUtils jwtUtils, Environment env) {
        this.applicationContext = applicationContext;
        this.authorizationManager = authorizationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ValidCommandChannelInterceptor(jwtUtils));
        registration.interceptors(new SecurityContextChannelInterceptor());
    }

}
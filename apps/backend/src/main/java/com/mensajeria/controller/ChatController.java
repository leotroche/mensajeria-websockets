package com.mensajeria.controller;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.Message;
import com.mensajeria.utils.JwtUtils;
import com.mensajeria.service.ChatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
public class ChatController {
    private final ChatServiceImpl chatService;
    private final JwtUtils jwtUtils;

    public ChatController(ChatServiceImpl chatService, JwtUtils jwtUtils) {
        this.chatService = chatService;
        this.jwtUtils = jwtUtils;
    }

    @MessageMapping("/chat1")
    @SendTo("/topic/canal1")
    public Information getMessage(Message message, SimpMessageHeaderAccessor headerAccessor) {
        String token = jwtUtils.getJwtFromHeader(headerAccessor);
        return chatService.getInformationFromMessage(message, token);

    }
}

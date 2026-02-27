package com.mensajeria.controller;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.Message;
import com.mensajeria.utils.JwtUtils;
import com.mensajeria.service.ChatServiceImpl;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final ChatServiceImpl chatService;
    private final JwtUtils jwtUtils;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatServiceImpl chatService, JwtUtils jwtUtils, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.jwtUtils = jwtUtils;
        this.messagingTemplate = messagingTemplate; // se encarga de mandar mensajes
    }

    @MessageMapping("/chat/{channelId}")
    public void getMessage(@DestinationVariable String channelId, Message message, SimpMessageHeaderAccessor headerAccessor) {
        String token = jwtUtils.getJwtFromHeader(headerAccessor);

        Information information = chatService.getInformationFromMessage(message, token);

        messagingTemplate.convertAndSend("/topic/" + channelId, information);

    }
}

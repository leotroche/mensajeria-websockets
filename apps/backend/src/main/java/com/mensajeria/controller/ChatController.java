package com.mensajeria.controller;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.MessagePayload;
import com.mensajeria.utils.JwtUtils;
import com.mensajeria.service.ChatServiceImpl;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

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

    @MessageMapping("chat/{username}")
    public void getUserMessage(@DestinationVariable String username, MessagePayload messagePayload, Principal principal) {

        String senderName = principal.getName();
        Information information = chatService.getInformationFromUserMessage(username, messagePayload, senderName);

        messagingTemplate.convertAndSendToUser(username, "/queue/messages", information);

        messagingTemplate.convertAndSendToUser(senderName, "/queue/messages", information); // reboto mensaje

    }

    @MessageMapping("group/{channelId}")
    public void getGroupMessage(@DestinationVariable String channelId, MessagePayload messagePayload, Principal principal) {
        // Principal es el usuario logueado, nos dice sus datos gracias al handshake interceptor

        Information information = chatService.getInformationFromMessage(channelId, messagePayload, principal.getName());

        messagingTemplate.convertAndSend("/topic/" + channelId, information);
        // rebote de info
        messagingTemplate.convertAndSend("/topic/" + information.senderId(), information); // TODO tal vez haya que cambiar esto por algo que no sea senderId


    }
}

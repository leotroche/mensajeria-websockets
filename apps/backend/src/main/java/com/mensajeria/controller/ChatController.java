package com.mensajeria.controller;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.InformationDraft;
import com.mensajeria.model.chat.MessagePayload;
import com.mensajeria.utils.JwtUtils;
import com.mensajeria.service.ChatServiceImpl;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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

//    @MessageMapping("chat/{receiverName}")
//    public void getUserMessage(@DestinationVariable String receiverName, MessagePayload messagePayload, SimpMessageHeaderAccessor headerAccessor) {
//
//        String token = jwtUtils.getJwtFromHeader(headerAccessor);
//
//        Information informationReceiver = chatService.getInformationFromUserMessageToReceiver(receiverName, messagePayload, token);
//        Information informationSender = chatService.getInformationFromUserMessageToSender(receiverName, messagePayload, token);
//
//        messagingTemplate.convertAndSend("/user/" + receiverName + "/queue/messages", informationReceiver);
//
//        messagingTemplate.convertAndSend("/user/" + informationSender.senderId() + "/queue/messages", informationSender); // reboto mensaje
//
//    }

    @MessageMapping("conversation/{conversationId}")
    public void getGroupMessage(@DestinationVariable String conversationId, MessagePayload messagePayload, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Got message for channel " + conversationId + ":" + messagePayload);

        String token = jwtUtils.getJwtFromHeader(headerAccessor);

        InformationDraft informationDraft = chatService.getInformationFromMessage(messagePayload, token);

        Information informationToDestiny = Information.fromDraft(informationDraft, informationDraft.senderId());
        Information informationBounce = Information.fromDraft(informationDraft, conversationId);

        messagingTemplate.convertAndSend("/conversation/" + conversationId + "/messages", informationToDestiny);

        // rebote de info
        messagingTemplate.convertAndSend("/conversation/" + informationDraft.senderId() + "/messages", informationBounce); // TODO tal vez haya que cambiar esto por algo que no sea senderId

    }
}

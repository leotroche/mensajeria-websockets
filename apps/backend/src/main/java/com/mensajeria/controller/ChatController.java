package com.mensajeria.controller;

import com.mensajeria.model.information.Information;
import com.mensajeria.model.information.chat.message.Message;
import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.model.information.chat.message.MessagePayload;
import com.mensajeria.utils.JwtUtils;
import com.mensajeria.service.ChatServiceImpl;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
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

    @MessageMapping("chats/request")
    public void getFriendRequest(@DestinationVariable String receiverName, MessagePayload messagePayload, SimpMessageHeaderAccessor headerAccessor) {

        String token = jwtUtils.getJwtFromHeader(headerAccessor);



    }

    @MessageMapping("chats/{chatId}")
    public void getMessage(@DestinationVariable String chatId, MessagePayload messagePayload, SimpMessageHeaderAccessor headerAccessor, Authentication auth) {
        // TODO probar Authentication
        System.out.println("Got message for channel " + chatId + ":" + messagePayload);

        // Get token for analysis

        String token = jwtUtils.getJwtFromHeader(headerAccessor);

        // Generate message (and bounce)

        MessageDraft messageDraft = chatService.getInformationFromMessage(messagePayload, token);

        Message messageToDestiny = Message.fromDraft(messageDraft, messageDraft.senderId());
        Message messageBounce = Message.fromDraft(messageDraft, chatId);

        // Send the information

        Information information = new Information( messageToDestiny);
        messagingTemplate.convertAndSend("/chats/" + chatId + "/queue", information);

        // rebote de info
//        information.setPayload(messageBounce);
        information = new Information( messageBounce);
        messagingTemplate.convertAndSend("/chats/" + messageDraft.senderId() + "/queue", information); // TODO tal vez haya que cambiar esto por algo que no sea senderId

    }
}

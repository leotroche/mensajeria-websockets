package com.mensajeria.controller.chat;

import com.mensajeria.model.information.Information;
import com.mensajeria.model.information.chat.message.Message;
import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.controller.dto.payload.MessagePayload;
import com.mensajeria.utils.JwtUtils;
import com.mensajeria.service.MessageServiceImpl;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    private final MessageServiceImpl chatService;
    private final JwtUtils jwtUtils;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatUtils chatUtils;

    public MessageController(MessageServiceImpl chatService, JwtUtils jwtUtils, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.jwtUtils = jwtUtils;
        this.messagingTemplate = messagingTemplate; // se encarga de mandar mensajes
        this.chatUtils = new ChatUtils(messagingTemplate);
    }


    @MessageMapping("chats/{chatId}")
    public void getMessage(@DestinationVariable String chatId, MessagePayload messagePayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Got message for channel " + chatId + ":" + messagePayload);

        // Get auth for analysis

        Authentication authentication = jwtUtils.validateAndGetAuthFromHeader(headerAccessor);

        // Generate message (and bounce)

        MessageDraft messageDraft = chatService.getInformationFromMessage(messagePayload, authentication);

        Message messageToDestiny = Message.fromDraft(messageDraft, messageDraft.senderId());
        Message messageBounce = Message.fromDraft(messageDraft, chatId);

        // Send the information

        Information information = new Information(messageToDestiny);
        chatUtils.sendToDestinatary(chatId, information);

        // rebote de info
        information = new Information(messageBounce);
        chatUtils.sendToDestinatary(messageDraft.senderId(), information);

    }

}

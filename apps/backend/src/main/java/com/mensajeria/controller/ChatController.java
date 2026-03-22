package com.mensajeria.controller;

import com.mensajeria.controller.dto.payload.RequestSendPayload;
import com.mensajeria.model.information.Information;
import com.mensajeria.model.information.InformationPayload;
import com.mensajeria.model.information.chat.message.Message;
import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.controller.dto.payload.MessagePayload;
import com.mensajeria.controller.dto.payload.RequestAcceptPayload;
import com.mensajeria.model.information.chat.request.SendRequest;
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


    @MessageMapping("requests/send")
    public void getFriendRequest(RequestSendPayload requestSendPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone sent a friend request");

        jwtUtils.validateAndGetAuthFromHeader(headerAccessor);

        SendRequest request = chatService.getInformationForRequest(requestSendPayload);

        Information information = new Information(request);
        sendToDestinatary(requestSendPayload.receiverId(), information);

    }

    @MessageMapping("requests/accept")
    public void acceptFriendRequest(RequestAcceptPayload requestAcceptPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone sent / answered a friend request to " + requestAcceptPayload.receiverId() + ":" + requestAcceptPayload);

        Authentication authentication = jwtUtils.validateAndGetAuthFromHeader(headerAccessor);

        InformationPayload request = chatService.getInformationForAcceptRequest(requestAcceptPayload);

        Information information = new Information(request);
        sendToDestinatary(requestAcceptPayload.receiverId(), information);

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
        sendToDestinatary(chatId, information);

        // rebote de info
//        information.setPayload(messageBounce);
        information = new Information(messageBounce);
        sendToDestinatary(messageDraft.senderId(), information);

    }

    private void sendToDestinatary(String receiverName, Information information) {
        messagingTemplate.convertAndSend("/chats/" + receiverName + "/queue", information);
    }
}

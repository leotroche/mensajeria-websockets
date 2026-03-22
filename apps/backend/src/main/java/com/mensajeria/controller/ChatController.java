package com.mensajeria.controller;

import com.mensajeria.controller.dto.payload.ChatPayload;
import com.mensajeria.model.information.Information;
import com.mensajeria.model.information.InformationPayload;
import com.mensajeria.model.information.chat.message.Message;
import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.controller.dto.payload.MessagePayload;
import com.mensajeria.controller.dto.payload.RequestAcceptRejectPayload;
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

//    @MessageMapping("chats/requests/{receiverId}")
//    public void getFriendRequest(@DestinationVariable String receiverId, RequestPayload requestPayload, SimpMessageHeaderAccessor headerAccessor) {
//
//        System.out.println("Someone sent / answered a friend request to " + receiverId + ":" + requestPayload);
//
//        Authentication authentication = jwtUtils.getAuthFromHeader(headerAccessor);
//
//        Request request = chatService.getInformationFromRequest(requestPayload, authentication);
//
//        Information information = new Information(request);
//        sendToDestinatary(receiverId, information);
//
//    }

    @MessageMapping("chats/requests/send")
    public void getFriendRequest(ChatPayload chatPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone sent a friend request");

        Authentication authentication = jwtUtils.getAuthFromHeader(headerAccessor);

        SendRequest request = chatService.getInformationForRequest(chatPayload, authentication);

        Information information = new Information(request);
        sendToDestinatary(chatPayload.receiverId(), information);

    }

    @MessageMapping("chats/requests/accept")
    public void acceptFriendRequest(RequestAcceptRejectPayload requestAcceptRejectPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone sent / answered a friend request to " + requestAcceptRejectPayload.receiverId() + ":" + requestAcceptRejectPayload);

        Authentication authentication = jwtUtils.getAuthFromHeader(headerAccessor);

        InformationPayload request = chatService.getInformationForAcceptRequest(requestAcceptRejectPayload, authentication);

        Information information = new Information(request);
        sendToDestinatary(requestAcceptRejectPayload.receiverId(), information);

    }

    @MessageMapping("chats/requests/reject")
    public void rejectFriendRequest(RequestAcceptRejectPayload requestAcceptRejectPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone sent / answered a friend request to " + requestAcceptRejectPayload.receiverId() + ":" + requestAcceptRejectPayload);

        Authentication authentication = jwtUtils.getAuthFromHeader(headerAccessor);

        InformationPayload request = chatService.getInformationForRejectRequest(requestAcceptRejectPayload, authentication);

        Information information = new Information(request);
        sendToDestinatary(requestAcceptRejectPayload.receiverId(), information);

    }

    @MessageMapping("chats/{chatId}")
    public void getMessage(@DestinationVariable String chatId, MessagePayload messagePayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Got message for channel " + chatId + ":" + messagePayload);

        // Get auth for analysis

        Authentication authentication = jwtUtils.getAuthFromHeader(headerAccessor);

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

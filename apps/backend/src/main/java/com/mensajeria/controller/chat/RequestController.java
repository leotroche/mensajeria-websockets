package com.mensajeria.controller.chat;

import com.mensajeria.controller.dto.payload.RequestSendPayload;
import com.mensajeria.model.information.Information;
import com.mensajeria.controller.dto.payload.InformationPayload;
import com.mensajeria.controller.dto.payload.RequestAcceptConfirmPayload;
import com.mensajeria.model.information.chat.request.SendRequest;
import com.mensajeria.service.RequestServiceImpl;
import com.mensajeria.utils.JwtUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RequestController {
    private final RequestServiceImpl requestService;
    private final JwtUtils jwtUtils;
    private final SimpMessagingTemplate messagingTemplate;

    public RequestController(RequestServiceImpl requestService, JwtUtils jwtUtils, SimpMessagingTemplate messagingTemplate) {
        this.requestService = requestService;
        this.jwtUtils = jwtUtils;
        this.messagingTemplate = messagingTemplate; // se encarga de mandar mensajes
    }


    @MessageMapping("requests/send")
    public void getFriendRequest(RequestSendPayload requestSendPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone sent a friend request");

        jwtUtils.validateAndGetAuthFromHeader(headerAccessor);

        SendRequest request = requestService.getInformationForRequest(requestSendPayload);

        Information information = new Information(request);
        sendToDestinatary(requestSendPayload.receiverId(), information);

    }

    @MessageMapping("requests/accept")
    public void acceptFriendRequest(RequestAcceptConfirmPayload requestAcceptConfirmPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone answered a friend request to " + requestAcceptConfirmPayload.receiverId());

        jwtUtils.validateAndGetAuthFromHeader(headerAccessor);

        InformationPayload request = requestService.getInformationForAcceptRequest(requestAcceptConfirmPayload);

        Information information = new Information(request);
        sendToDestinatary(requestAcceptConfirmPayload.receiverId(), information);

    }

    @MessageMapping("requests/accept/confirm")
    public void confirmFriendRequest(RequestAcceptConfirmPayload requestAcceptConfirmPayload, SimpMessageHeaderAccessor headerAccessor) {

        System.out.println("Someone answered a friend request to " + requestAcceptConfirmPayload.receiverId());

        jwtUtils.validateAndGetAuthFromHeader(headerAccessor);

        InformationPayload request = requestService.getInformationForConfirmRequest(requestAcceptConfirmPayload);

        Information information = new Information(request);
        sendToDestinatary(requestAcceptConfirmPayload.receiverId(), information);

    }

    private void sendToDestinatary(String receiverName, Information information) {
        messagingTemplate.convertAndSend("/chats/" + receiverName + "/queue", information);
    }
}

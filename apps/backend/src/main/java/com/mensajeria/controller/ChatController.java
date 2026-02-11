package com.mensajeria.controller;

import com.mensajeria.model.websocket.Information;
import com.mensajeria.model.websocket.Message;
import com.mensajeria.model.websocket.MessageStatus;
import com.mensajeria.service.ChatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
public class ChatController {
    ChatServiceImpl chatService;

    public ChatController(ChatServiceImpl chatServiceP) {
        chatService = chatServiceP;
    }

    @MessageMapping("/chat1")
    @SendTo("/topic/canal1")
    public Information getMessage(Message message) {

        return chatService.getInformationFromMessage(message);
    }
}

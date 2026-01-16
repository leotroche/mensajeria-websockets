package com.mensajeria.controller;

import com.mensajeria.websocket.Information;
import com.mensajeria.websocket.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat1")
    @SendTo("/topic/canal1")
    public Information getMessage(Message message) {
        System.out.println(message);
        return new Information(message.body());
    }
}

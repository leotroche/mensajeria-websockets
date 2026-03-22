package com.mensajeria.controller.chat;

import com.mensajeria.model.information.Information;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class ChatUtils {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatUtils(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    void sendToDestinatary(String receiverName, Information information) {
        messagingTemplate.convertAndSend("/chats/" + receiverName + "/queue", information);
    }
}

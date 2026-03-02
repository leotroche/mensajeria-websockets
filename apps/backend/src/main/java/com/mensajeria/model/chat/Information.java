package com.mensajeria.model.chat;

// Information es lo que se envía al user
public record Information(
        String id,
        String chatId,
        String senderId,
        String content,
        String createdAt
) {
}

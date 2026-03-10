package com.mensajeria.model.information.chat.message;

public record MessageDraft(
        String id,
        String senderId,

        String content,
        String createdAt
) {
}

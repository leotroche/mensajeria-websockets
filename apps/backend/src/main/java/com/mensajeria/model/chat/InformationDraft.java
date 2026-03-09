package com.mensajeria.model.chat;

public record InformationDraft(
        String id,
        String senderId,

        String content,
        String createdAt
) {
}

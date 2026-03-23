package com.mensajeria.model.information.chat.message;

import com.mensajeria.controller.dto.payload.InformationPayload;

// es lo que se envía al user
public record Message(
        String id,
        String chatId,
        String senderId,

        String content,
        Long createdAt
) implements InformationPayload {
    public static Message fromDraft(MessageDraft messageDraft, String chatId) {
        return new Message(messageDraft.id(), chatId, messageDraft.senderId(), messageDraft.content(), messageDraft.createdAt());
    }
}

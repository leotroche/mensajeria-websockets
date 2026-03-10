package com.mensajeria.model.information.chat.message;

import com.mensajeria.model.information.InformationPayload;

// es lo que se envía al user
public record Message(
        String id,
        String chatId,
        String senderId,

        String content,
        String createdAt
) implements InformationPayload {
    public static Message fromDraft(MessageDraft messageDraft, String chatId) {
        return new Message(messageDraft.id(), chatId, messageDraft.senderId(), messageDraft.content(), messageDraft.createdAt());
    }
}

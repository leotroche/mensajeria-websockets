package com.mensajeria.model.chat;

// Information es lo que se envía al user
public record Information(
        String id,
        String conversationId,
        String senderId,

        String content,
        String createdAt
) {
    public static Information fromDraft(InformationDraft informationDraft, String conversationId) {
        return new Information(informationDraft.id(), conversationId, informationDraft.senderId(), informationDraft.content(), informationDraft.createdAt());
    }
}

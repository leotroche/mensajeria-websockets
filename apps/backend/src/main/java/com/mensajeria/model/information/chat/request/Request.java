package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;

public record Request(
        String id,
        String senderId,
        String senderName,
        String chatId,
        RequestStatus status,
        Long createdAt
) implements InformationPayload {
}

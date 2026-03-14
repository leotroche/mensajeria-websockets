package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;

public record Request(
        String id,
        String senderId,
        String senderName,
        Long createdAt
) implements InformationPayload {
}

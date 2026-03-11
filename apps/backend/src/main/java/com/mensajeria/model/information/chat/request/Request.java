package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;

public record Request(
        String id,
        String senderId,
        Long createdAt
) implements InformationPayload {
}

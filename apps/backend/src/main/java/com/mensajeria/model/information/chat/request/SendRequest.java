package com.mensajeria.model.information.chat.request;

import com.mensajeria.controller.dto.payload.InformationPayload;
import com.mensajeria.model.Contact;

public record SendRequest(
        String id,
        Contact sender,
        Long createdAt
) implements InformationPayload {
}

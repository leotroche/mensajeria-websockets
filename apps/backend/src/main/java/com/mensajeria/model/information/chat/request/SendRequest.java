package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;
import com.mensajeria.model.information.chat.Chat;
import com.mensajeria.model.information.contact.Contact;

public record SendRequest(
        String id,
        Contact sender,
        Long createdAt
) implements InformationPayload {
}

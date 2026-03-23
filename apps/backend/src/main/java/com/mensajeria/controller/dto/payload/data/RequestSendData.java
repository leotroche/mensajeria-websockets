package com.mensajeria.controller.dto.payload.data;

import com.mensajeria.model.information.contact.Contact;

public record RequestSendData(String id,
                              Contact sender,
                              Long createdAt
) {
}

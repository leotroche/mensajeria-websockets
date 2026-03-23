package com.mensajeria.controller.dto.payload.data;

import com.mensajeria.model.Contact;

public record RequestSendData(String requestId,
                              Contact contact,
                              Long createdAt
) {
}

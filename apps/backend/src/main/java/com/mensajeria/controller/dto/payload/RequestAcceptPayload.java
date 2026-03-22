package com.mensajeria.controller.dto.payload;

import com.mensajeria.model.information.chat.Chat;
import com.mensajeria.model.information.contact.Contact;

public record RequestAcceptPayload(
        Chat chat,
        String receiverId
) {

}
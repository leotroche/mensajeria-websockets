package com.mensajeria.controller.dto.payload;

import com.mensajeria.model.information.chat.Chat;

public record ChatPayload(
        String receiverId,
        Chat chat
) {

}
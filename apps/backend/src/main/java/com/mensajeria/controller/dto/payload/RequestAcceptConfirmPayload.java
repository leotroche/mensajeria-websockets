package com.mensajeria.controller.dto.payload;

import com.mensajeria.controller.dto.payload.data.RequestAcceptData;
import com.mensajeria.model.information.chat.Chat;

public record RequestAcceptConfirmPayload(
        Chat data,
        String receiverId
) {

}
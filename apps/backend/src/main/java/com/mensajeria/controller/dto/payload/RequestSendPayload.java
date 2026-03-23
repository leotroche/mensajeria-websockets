package com.mensajeria.controller.dto.payload;

import com.mensajeria.controller.dto.payload.data.RequestSendData;
import com.mensajeria.model.Contact;

public record RequestSendPayload(
        RequestSendData data,
        String receiverId
) {

}
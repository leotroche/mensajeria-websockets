package com.mensajeria.controller.dto.payload;

import com.mensajeria.controller.dto.payload.data.RequestSendData;

public record RequestSendPayload(
        RequestSendData data,
        String receiverId
) {

}
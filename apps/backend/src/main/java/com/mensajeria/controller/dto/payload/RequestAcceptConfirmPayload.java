package com.mensajeria.controller.dto.payload;

import com.mensajeria.controller.dto.payload.data.RequestAcceptData;

public record RequestAcceptConfirmPayload(
        RequestAcceptData data,
        String receiverId
) {

}
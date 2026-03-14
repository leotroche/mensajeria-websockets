package com.mensajeria.model.information.chat.request;

public record RequestPayload(
        String id,
        String senderName,
        String receiverId,
        RequestStatus status
) {

}
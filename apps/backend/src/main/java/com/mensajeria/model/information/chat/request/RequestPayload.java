package com.mensajeria.model.information.chat.request;

public record RequestPayload(
        String id,
        String senderId,
        String senderName,
        RequestStatus status
) {

}
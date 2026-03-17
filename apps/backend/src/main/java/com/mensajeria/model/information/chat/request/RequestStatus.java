package com.mensajeria.model.information.chat.request;

public enum RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED;

    public Request getRequestFromPayload(RequestPayload payload, String senderName, Long createdAt, String chatId) {

        switch (this) {
            case PENDING -> {
                return new Request(payload.id(), senderName, senderName, null, PENDING, createdAt);
            }
            case ACCEPTED -> {
                return new Request(payload.id(), senderName, senderName, chatId, ACCEPTED, createdAt);
            }
            case REJECTED -> {
                return new Request(payload.id(), senderName, senderName, null, REJECTED, createdAt);
            }
            default -> throw new IllegalArgumentException("Not a valid request status");
        }
        // TODO tal vez haya que cambiar para que sea senderId senderName

    }
}
package com.mensajeria.model.websocket;
// Message es lo que se recibe
public record Message(
        String senderName,
        String body
) {
}

package com.mensajeria.model.chat;
// Message es lo que se recibe
public record Message(
        String senderName,
        String body
) {
}

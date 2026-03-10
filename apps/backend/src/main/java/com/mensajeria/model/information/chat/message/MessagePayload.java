package com.mensajeria.model.information.chat.message;
// es lo que se recibe
public record MessagePayload(
        String id,
        String content
) {
}

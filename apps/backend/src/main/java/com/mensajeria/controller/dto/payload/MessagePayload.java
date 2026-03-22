package com.mensajeria.controller.dto.payload;
// es lo que se recibe
public record MessagePayload(
        String id,
        String content
) {
}

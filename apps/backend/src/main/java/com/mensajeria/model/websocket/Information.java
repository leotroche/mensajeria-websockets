package com.mensajeria.model.websocket;
import java.time.LocalDate;

// Information es lo que se envía al user
public record Information(
        String userId,
        String text
) {
}
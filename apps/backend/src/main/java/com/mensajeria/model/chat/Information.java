package com.mensajeria.model.chat;
import java.time.LocalDate;

// Information es lo que se envía al user
public record Information(
        String id,
        String text,
        LocalDate time,
        MessageStatus status
) {
}

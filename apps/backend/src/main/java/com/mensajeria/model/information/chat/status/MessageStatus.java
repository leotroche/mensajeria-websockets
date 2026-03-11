package com.mensajeria.model.information.chat.status;

import com.mensajeria.model.information.InformationPayload;
import com.mensajeria.model.information.chat.message.Status;

public record MessageStatus (
        String id,
        String chatId,
        String senderId,
        Status status
) implements InformationPayload {
}

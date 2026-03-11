package com.mensajeria.model.information.chat.status;

import com.mensajeria.model.information.chat.message.Status;

public record MessageStatusPayload(
        String id,
        Status status
) {

}

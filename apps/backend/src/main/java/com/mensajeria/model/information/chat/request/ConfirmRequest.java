package com.mensajeria.model.information.chat.request;

import com.mensajeria.controller.dto.payload.InformationPayload;
import com.mensajeria.model.information.chat.Chat;

public record ConfirmRequest (Chat chat)
        implements InformationPayload {
}
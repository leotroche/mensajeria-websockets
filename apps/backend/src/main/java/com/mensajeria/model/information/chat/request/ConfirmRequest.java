package com.mensajeria.model.information.chat.request;

import com.mensajeria.controller.dto.payload.InformationPayload;
import com.mensajeria.model.information.chat.Chat;
import com.mensajeria.model.information.chat.ChatInterface;

public record ConfirmRequest (Chat chat)
        implements InformationPayload {
}
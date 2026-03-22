package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;
import com.mensajeria.model.information.chat.Chat;

public record AcceptRequest (Chat chat)
        implements InformationPayload {
}

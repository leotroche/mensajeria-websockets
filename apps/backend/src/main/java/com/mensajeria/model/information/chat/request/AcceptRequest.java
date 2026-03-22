package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;

public record AcceptRequest (String id, Long createdAt)
        implements InformationPayload {
}

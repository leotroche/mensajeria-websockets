package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;

public record RejectRequest(
        String id,
                            String senderName,
                            RequestStatus status,
                            Long createdAt) implements InformationPayload {
}

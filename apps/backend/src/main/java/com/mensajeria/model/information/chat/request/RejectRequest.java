package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;

public record RejectRequest(
                            String id,
                            String senderName,
                            Long createdAt) implements InformationPayload {
}

package com.mensajeria.model.information.chat.request;

public record RejectRequest(
        String id,
                            String senderName,
                            RequestStatus status,
                            Long createdAt) implements Request{
}

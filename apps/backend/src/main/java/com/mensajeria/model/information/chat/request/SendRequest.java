package com.mensajeria.model.information.chat.request;

public record SendRequest(
                            String id,
                            String senderName,
                            RequestStatus status,
                            Long createdAt) implements Request {
}

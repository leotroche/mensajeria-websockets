package com.mensajeria.controller.dto.payload.data;

public record RequestSendData(String requestId,
                              String senderChatId,
                              String senderName,
                              Long createdAt
) {
}

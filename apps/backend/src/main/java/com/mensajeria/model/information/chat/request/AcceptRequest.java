package com.mensajeria.model.information.chat.request;

import com.mensajeria.model.information.InformationPayload;

public record AcceptRequest (String id,
                             String senderId,
                             String senderName,
                             String chatId,
                             RequestStatus status,
                             Long createdAt) implements InformationPayload {
}

package com.mensajeria.model.information.chat.request;

public record AcceptRequest (String id,
                             String senderId,
                             String senderName,
                             String chatId,
                             RequestStatus status,
                             Long createdAt) implements Request{
}

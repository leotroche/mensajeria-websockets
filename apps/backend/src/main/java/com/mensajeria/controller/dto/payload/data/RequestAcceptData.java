package com.mensajeria.controller.dto.payload.data;


import com.mensajeria.model.information.chat.ChatInterface;

public record RequestAcceptData(String id,
                                String name,
                                String avatar,
                                long createdAt,
                                long updatedAt,
                                int unreadCount,
                                String lastMessage) implements ChatInterface {
}

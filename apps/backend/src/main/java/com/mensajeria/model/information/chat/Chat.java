package com.mensajeria.model.information.chat;

public record Chat(
        String id,
        String name,
        String avatar,
        long createdAt,
        long updatedAt,
        int unreadCount,
        String lastMessage
) implements ChatInterface{}

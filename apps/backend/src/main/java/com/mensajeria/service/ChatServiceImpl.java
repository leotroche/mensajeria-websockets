package com.mensajeria.service;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.MessagePayload;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ChatServiceImpl {

    private final UserDAOJPA userDAOJPA;
    private final JwtUtils jwtUtils;

    public ChatServiceImpl(UserDAOJPA userDAO, JwtUtils jwtUtils) {
        userDAOJPA = userDAO;
        this.jwtUtils = jwtUtils;
    }

    public Information getInformationFromMessage(String channelId, MessagePayload messagePayload, String token) {
        System.out.println("Got message for channel " + channelId + ":" + messagePayload);

        String senderName = jwtUtils.getAuthentication(token).getName();
        Optional<UserRepositoryJPA> user = userDAOJPA.findById(senderName);
        if (user.isEmpty()) throw new UserNotFoundException("User " + senderName + " not found.");

        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        return new Information(messagePayload.id(), channelId, senderName, messagePayload.content(), createdAt);
    }

    public Information getInformationFromUserMessage(String receiverName, MessagePayload messagePayload, String token) {
        System.out.println("Got message for user " + receiverName + ":" + messagePayload);

        String senderName = jwtUtils.getAuthentication(token).getName();
        Optional<UserRepositoryJPA> user = userDAOJPA.findById(senderName);
        if (user.isEmpty()) throw new UserNotFoundException("User " + senderName + " not found.");

        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

        return new Information(messagePayload.id(), receiverName, senderName, messagePayload.content(), createdAt);
    }
}

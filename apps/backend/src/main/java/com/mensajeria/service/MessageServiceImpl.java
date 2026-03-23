package com.mensajeria.service;

import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.controller.dto.payload.MessagePayload;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class MessageServiceImpl {

    private final UserDAOJPA userDAOJPA;

    public MessageServiceImpl(UserDAOJPA userDAO) {
        userDAOJPA = userDAO;
    }

    public MessageDraft getInformationFromMessage(MessagePayload messagePayload, Authentication auth) {

        String senderName = getAuthName(auth);

        Long createdAt = Instant.now().toEpochMilli();

        return new MessageDraft(messagePayload.id(), senderName, messagePayload.content(), createdAt);
    }


    private @NonNull String getAuthName(Authentication auth) {
        String senderName = auth.getName();
        Optional<UserRepositoryJPA> user = userDAOJPA.findById(senderName);
        if (user.isEmpty()) throw new UserNotFoundException("User " + senderName + " not found.");
        return senderName;
    }

}

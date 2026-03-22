package com.mensajeria.service;

import com.mensajeria.controller.dto.payload.ChatPayload;
import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.controller.dto.payload.MessagePayload;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.model.information.chat.request.AcceptRequest;
import com.mensajeria.model.information.chat.request.RejectRequest;
import com.mensajeria.controller.dto.payload.RequestAcceptRejectPayload;
import com.mensajeria.model.information.chat.request.SendRequest;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ChatServiceImpl {

    private final UserDAOJPA userDAOJPA;

    public ChatServiceImpl(UserDAOJPA userDAO) {
        userDAOJPA = userDAO;
    }

    public MessageDraft getInformationFromMessage(MessagePayload messagePayload, Authentication auth) {

        String senderName = getAuthName(auth);

        Long createdAt = Instant.now().toEpochMilli();

        return new MessageDraft(messagePayload.id(), senderName, messagePayload.content(), createdAt);
    }

    public AcceptRequest getInformationForAcceptRequest(RequestAcceptRejectPayload requestAcceptRejectPayload, Authentication auth) {
        String senderName = getAuthName(auth);

        Long createdAt = Instant.now().toEpochMilli();

        return new AcceptRequest(requestAcceptRejectPayload.id(), createdAt);
    }

    public RejectRequest getInformationForRejectRequest(RequestAcceptRejectPayload requestAcceptRejectPayload, Authentication auth) {
        String senderName = getAuthName(auth);

        Long createdAt = Instant.now().toEpochMilli();

        return new RejectRequest(requestAcceptRejectPayload.id(), senderName, createdAt);
        // TODO acá receiverId se vuelve chatId, habría que tomar chatId posiblemente de otro lado
    }

    public SendRequest getInformationForRequest(ChatPayload chatPayload, Authentication auth) {

        Long createdAt = Instant.now().toEpochMilli();

        return new SendRequest(chatPayload.chat(), createdAt);
        // TODO acá receiverId se vuelve chatId, habría que tomar chatId posiblemente de otro lado
    }



    private @NonNull String getAuthName(Authentication auth) {
        String senderName = auth.getName();
        Optional<UserRepositoryJPA> user = userDAOJPA.findById(senderName);
        if (user.isEmpty()) throw new UserNotFoundException("User " + senderName + " not found.");
        return senderName;
    }

}

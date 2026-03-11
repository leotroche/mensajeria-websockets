package com.mensajeria.service;

import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.model.information.chat.message.MessagePayload;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.model.information.chat.request.Request;
import com.mensajeria.model.information.chat.request.RequestPayload;
import com.mensajeria.model.information.chat.status.MessageStatus;
import com.mensajeria.model.information.chat.status.MessageStatusPayload;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.utils.JwtUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ChatServiceImpl {

    private final UserDAOJPA userDAOJPA;
    private final JwtUtils jwtUtils;

    public ChatServiceImpl(UserDAOJPA userDAO, JwtUtils jwtUtils) {
        userDAOJPA = userDAO;
        this.jwtUtils = jwtUtils;
    }

    public MessageDraft getInformationFromMessage(MessagePayload messagePayload, Authentication auth) {

        String senderName = getAuthName(auth);

        Long createdAt = Instant.now().toEpochMilli();

        return new MessageDraft(messagePayload.id(), senderName, messagePayload.content(), createdAt);
    }

    public Request getInformationFromRequest(RequestPayload requestPayload, Authentication auth) {
        String senderName = getAuthName(auth);

        Long createdAt = Instant.now().toEpochMilli();

        return new Request(requestPayload.id(), senderName, createdAt);

    }

    private @NonNull String getAuthName(Authentication auth) {
        String senderName = auth.getName();
        Optional<UserRepositoryJPA> user = userDAOJPA.findById(senderName);
        if (user.isEmpty()) throw new UserNotFoundException("User " + senderName + " not found.");
        return senderName;
    }

    public Request getInformationFromStatus(MessageStatusPayload messageStatusPayload, Authentication auth) {
        String senderName = getAuthName(auth);

        return new MessageStatus(messageStatusPayload.id(), senderName, );
    }

//    public Information getInformationFromUserMessageToSender(String receiverName, MessagePayload messagePayload, String token) {
//        System.out.println("Got message for user " + receiverName + ":" + messagePayload);
//
//        String senderName = jwtUtils.getAuthentication(token).getName();
//        Optional<UserRepositoryJPA> user = userDAOJPA.findById(senderName);
//        if (user.isEmpty()) throw new UserNotFoundException("User " + senderName + " not found.");
//
//        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
//
//        return new Information(messagePayload.id(), receiverName, senderName, messagePayload.content(), createdAt);
//    }
//
//    public Information getInformationFromUserMessageToReceiver(String receiverName, MessagePayload messagePayload, String token) {
//        System.out.println("Got message for user " + receiverName + ":" + messagePayload);
//
//        String senderName = jwtUtils.getAuthentication(token).getName();
//        Optional<UserRepositoryJPA> user = userDAOJPA.findById(senderName);
//        if (user.isEmpty()) throw new UserNotFoundException("User " + senderName + " not found.");
//
//        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
//
//        return new Information(messagePayload.id(), senderName, senderName, messagePayload.content(), createdAt);
//    }
}

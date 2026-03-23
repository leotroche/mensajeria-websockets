package com.mensajeria.service;

import com.mensajeria.controller.dto.payload.MessagePayload;
import com.mensajeria.controller.dto.payload.RequestAcceptPayload;
import com.mensajeria.controller.dto.payload.RequestSendPayload;
import com.mensajeria.controller.dto.payload.data.RequestSendData;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.model.information.chat.message.MessageDraft;
import com.mensajeria.model.information.chat.request.AcceptRequest;
import com.mensajeria.model.information.chat.request.SendRequest;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RequestServiceImpl {

    private final UserDAOJPA userDAOJPA;

    public RequestServiceImpl(UserDAOJPA userDAO) {
        userDAOJPA = userDAO;
    }

    public AcceptRequest getInformationForAcceptRequest(RequestAcceptPayload requestAcceptPayload) {

        return new AcceptRequest(requestAcceptPayload.data().chat());
    }

    public SendRequest getInformationForRequest(RequestSendPayload requestSendPayload) {

        RequestSendData data = requestSendPayload.data();

        return new SendRequest(data.id(), data.sender(), data.createdAt());
        // TODO acá receiverId se vuelve chatId, habría que tomar chatId posiblemente de otro lado
    }

}

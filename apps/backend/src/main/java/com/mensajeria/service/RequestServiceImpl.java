package com.mensajeria.service;

import com.mensajeria.controller.dto.payload.RequestAcceptConfirmPayload;
import com.mensajeria.controller.dto.payload.RequestSendPayload;
import com.mensajeria.controller.dto.payload.data.RequestSendData;
import com.mensajeria.model.Contact;
import com.mensajeria.model.information.chat.request.AcceptRequest;
import com.mensajeria.model.information.chat.request.ConfirmRequest;
import com.mensajeria.model.information.chat.request.SendRequest;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl {

    private final UserDAOJPA userDAOJPA;

    public RequestServiceImpl(UserDAOJPA userDAO) {
        userDAOJPA = userDAO;
    }

    public AcceptRequest getInformationForAcceptRequest(RequestAcceptConfirmPayload requestAcceptConfirmPayload) {

        return new AcceptRequest(requestAcceptConfirmPayload.data().chat());
    }

    public ConfirmRequest getInformationForConfirmRequest(RequestAcceptConfirmPayload requestAcceptConfirmPayload) {

        return new ConfirmRequest(requestAcceptConfirmPayload.data().chat());
    }

    public SendRequest getInformationForRequest(RequestSendPayload requestSendPayload) {

        RequestSendData data = requestSendPayload.data();


        return new SendRequest(data.requestId(), new Contact(data.senderChatId(), data.senderName()), data.createdAt());
        // TODO acá receiverId se vuelve chatId, habría que tomar chatId posiblemente de otro lado
    }

}

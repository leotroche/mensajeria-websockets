package com.mensajeria.service;

import com.mensajeria.controller.dto.payload.RequestAcceptConfirmPayload;
import com.mensajeria.controller.dto.payload.RequestSendPayload;
import com.mensajeria.controller.dto.payload.data.RequestSendData;
import com.mensajeria.model.information.chat.request.AcceptRequest;
import com.mensajeria.model.information.chat.request.ConfirmRequest;
import com.mensajeria.model.information.chat.request.SendRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl {

    public RequestServiceImpl() {

    }

    public AcceptRequest getInformationForAcceptRequest(RequestAcceptConfirmPayload requestAcceptConfirmPayload) {
        return new AcceptRequest(requestAcceptConfirmPayload.data());
    }

    public ConfirmRequest getInformationForConfirmRequest(RequestAcceptConfirmPayload requestAcceptConfirmPayload) {

        return new ConfirmRequest(requestAcceptConfirmPayload.data());
    }

    public SendRequest getInformationForRequest(RequestSendPayload requestSendPayload) {

        RequestSendData data = requestSendPayload.data();

        return new SendRequest(data.requestId(), data.contact(), data.createdAt());
        // TODO acá receiverId se vuelve chatId, habría que tomar chatId posiblemente de otro lado
    }

}

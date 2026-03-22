package com.mensajeria.model.information;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mensajeria.model.information.chat.message.Message;
import com.mensajeria.model.information.chat.request.AcceptRequest;
import com.mensajeria.model.information.chat.request.SendRequest;

public record Information(
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type"

        )
        @JsonSubTypes({
                @JsonSubTypes.Type(value = Message.class, name = "message"),
                @JsonSubTypes.Type(value = SendRequest.class, name = "contact:request:sent"),
                @JsonSubTypes.Type(value = AcceptRequest.class, name = "contact:request:accepted")
        })
        InformationPayload payload
) {

}

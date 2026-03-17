package com.mensajeria.model.information;

import com.mensajeria.model.information.chat.message.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Information {
    private String type;
    private InformationPayload payload;

    public Information() {} // default constructor for Jackson

    public Information(String type, InformationPayload payload) {
        this.type = type;
        this.payload = payload;
    }

    public Information(InformationPayload payload) {
        this.payload = payload;
    }

}
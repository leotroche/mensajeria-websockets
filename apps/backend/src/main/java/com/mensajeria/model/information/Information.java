package com.mensajeria.model.information;

import com.mensajeria.model.information.chat.message.Message;

public record Information(
        String type,
        InformationPayload payload
) {

}

package com.mensajeria.model.information;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mensajeria.model.information.chat.message.Message;

// Esto es porque no es capaz de entender el polimorfismo de manera nativa, así que hay que decirle que si
// type = "nombreobjeto" significa tal clase, así convierte sin problema
// como ya lo setea automáticamente, no hay problema
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type"

)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Message.class, name = "message")
})
public interface InformationPayload {

}

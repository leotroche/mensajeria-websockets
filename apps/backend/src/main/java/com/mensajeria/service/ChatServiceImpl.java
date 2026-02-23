package com.mensajeria.service;

import com.mensajeria.model.websocket.Information;
import com.mensajeria.model.websocket.Message;
import com.mensajeria.model.websocket.MessageStatus;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ChatServiceImpl {

    private final UserDAOJPA userDAOJPA;

    public ChatServiceImpl(UserDAOJPA userDAO) {
        userDAOJPA = userDAO;
    }

    public Information getInformationFromMessage(Message message) {
        System.out.println("Got message for channel 1: " + message);


        Optional<UserRepositoryJPA> user = userDAOJPA.findById(message.senderName());
<<<<<<< HEAD
        if (user.isEmpty()) return new Information("0", message.body(), LocalDate.now(), MessageStatus.SENT);
=======
        if (user.isEmpty()) return new Information("0", "This message is not from a valid user.");
        // TODO cambiar por otra cosa
>>>>>>> dev

        String userId = String.valueOf(user.get().getId());

<<<<<<< HEAD
        return new Information(userId, message.body(), LocalDate.now(), MessageStatus.SENT);
=======
        return new Information(String.valueOf(userId), message.body());
>>>>>>> dev
    }
}

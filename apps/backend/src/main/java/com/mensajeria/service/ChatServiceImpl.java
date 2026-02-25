package com.mensajeria.service;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.Message;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatServiceImpl {

    private final UserDAOJPA userDAOJPA;
    private final JwtUtils jwtUtils;

    public ChatServiceImpl(UserDAOJPA userDAO, JwtUtils jwtUtils) {
        userDAOJPA = userDAO;
        this.jwtUtils = jwtUtils;
    }

    public Information getInformationFromMessage(Message message, String token) {
        System.out.println("Got message for channel 1: " + message);

        String username = jwtUtils.verifyThenGetUsernameFromJwtToken(token);

        Optional<UserRepositoryJPA> user = userDAOJPA.findById(username);
        if (user.isEmpty()) return new Information("0", "This message is not from a valid user.");
        // TODO cambiar por otra cosa


        String userId = String.valueOf(user.get().getId());


        return new Information(String.valueOf(userId), message.body());

    }
}

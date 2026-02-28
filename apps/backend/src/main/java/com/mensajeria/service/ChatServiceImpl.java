package com.mensajeria.service;

import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.MessagePayload;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ChatServiceImpl {

    private final UserDAOJPA userDAOJPA;
    private final JwtUtils jwtUtils;

    public ChatServiceImpl(UserDAOJPA userDAO, JwtUtils jwtUtils) {
        userDAOJPA = userDAO;
        this.jwtUtils = jwtUtils;
    }

    public Information getInformationFromMessage(String channelId, MessagePayload messagePayload, String token) {
        System.out.println("Got message for channel " + channelId + ":" + messagePayload);

        String username = jwtUtils.verifyThenGetUsernameFromJwtToken(token);

        Optional<UserRepositoryJPA> user = userDAOJPA.findById(username);
        if (user.isEmpty()) return new Information("0", "0", "This message is not from a valid user.", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        // TODO cambiar por otra cosa

        Long userId = user.get().getId();

        return new Information(channelId, String.valueOf(userId), messagePayload.content(), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}

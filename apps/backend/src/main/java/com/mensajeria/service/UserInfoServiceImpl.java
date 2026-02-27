package com.mensajeria.service;

import com.mensajeria.controller.dto.UserInfo;
import com.mensajeria.model.chat.Information;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoServiceImpl {

    private final UserDAOJPA userDAOJPA;
    private final JwtUtils jwtUtils;

    public UserInfoServiceImpl(UserDAOJPA userDAO, JwtUtils jwtUtils) {
        userDAOJPA = userDAO;
        this.jwtUtils = jwtUtils;
    }

    public UserInfo getUserInfo(String token) {
        String username = jwtUtils.verifyThenGetUsernameFromJwtToken(token);
        UserRepositoryJPA user = userDAOJPA.findById(username).get();
        Long id = user.getId();
        return new UserInfo(String.valueOf(id), user.getUsername());
    }
}

package com.mensajeria.service;

import com.mensajeria.controller.dto.userinfo.UserInfo;
import com.mensajeria.controller.dto.userinfo.UserInfoData;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.utils.JwtUtils;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl {

    private final UserDAOJPA userDAOJPA;
    private final JwtUtils jwtUtils;

    public UserInfoServiceImpl(UserDAOJPA userDAO, JwtUtils jwtUtils) {
        userDAOJPA = userDAO;
        this.jwtUtils = jwtUtils;
    }

    public UserInfo getUserInfo(String token) {
        String username;
        try {
           username = jwtUtils.verifyThenGetUsernameFromJwtToken(token);
        } catch (RuntimeException e) {
            throw new UserNotFoundException("Invalid credentials.");
        }

        UserRepositoryJPA user = userDAOJPA.findById(username).orElseThrow(() -> new UserNotFoundException("User of name " + username + " not found."));
        Long id = user.getId();

        UserInfoData data = new UserInfoData(String.valueOf(id),  user.getUsername());

        return new UserInfo(data);
    }
}

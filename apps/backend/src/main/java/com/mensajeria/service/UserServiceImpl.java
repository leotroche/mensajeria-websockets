package com.mensajeria.service;

import com.mensajeria.controller.dto.security.login.LoginRequest;
import com.mensajeria.controller.dto.security.login.LoginResponse;
import com.mensajeria.controller.dto.userinfo.UserInfo;
import com.mensajeria.controller.dto.userinfo.UserInfoData;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.persistency.dao.jdbc.UserDAOJDBC;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.utils.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {

    private final UserDAOJPA userDAOJPA;
    private final JwtUtils jwtUtils;
    private final UserDAOJDBC userDAOJDBC;

    private final SecurityServiceImpl securityService;


    public UserServiceImpl(UserDAOJPA userDAO, JwtUtils jwtUtils, UserDAOJDBC userDAOJDBC, PasswordEncoder passwordEncoder, SecurityServiceImpl securityService) {
        userDAOJPA = userDAO;
        this.jwtUtils = jwtUtils;
        this.userDAOJDBC = userDAOJDBC;
        this.securityService = securityService;
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

    public LoginResponse create(String username, String password) {
        // TODO chequeos a los strings de name y password
        userDAOJDBC.save(username, password);

        LoginRequest loginRequest = new LoginRequest(username, password);

        return securityService.authenticateUser(loginRequest);
    }
}

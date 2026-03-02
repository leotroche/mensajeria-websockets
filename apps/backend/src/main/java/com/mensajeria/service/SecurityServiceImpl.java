package com.mensajeria.service;

import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.utils.JwtUtils;
import com.mensajeria.controller.dto.security.LoginRequest;
import com.mensajeria.controller.dto.security.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecurityServiceImpl {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserDAOJPA userDAOJPA;

    public SecurityServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserDAOJPA userDAOJPA) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDAOJPA = userDAOJPA;
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            authentication = authenticationManager.authenticate(authenticationToken);

        } catch (AuthenticationException exception) {
            throw new UserNotFoundException("User or password is not valid.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) throw new UserNotFoundException("User not found.");

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        return new LoginResponse(jwtToken);
    }
}

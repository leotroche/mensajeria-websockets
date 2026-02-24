package com.mensajeria.service;

import com.mensajeria.persistency.dao.jpa.UserDAOJPA;
import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import com.mensajeria.security.jwt.JwtUtils;
import com.mensajeria.security.jwt.dto.LoginRequest;
import com.mensajeria.security.jwt.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new LoginResponse(map);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) throw new IllegalArgumentException("User not found");
        String userId = String.valueOf(userDAOJPA.findById(loginRequest.getUsername()).get().getId()); // TODO se puede mejorar?

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new LoginResponse(userDetails.getUsername(), userId, roles, jwtToken);
    }
}

package com.mensajeria.security.jwt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class LoginResponse {

    private Map<String, Object> errors;
    private String username;
    private String userId;
    private String token;
//    private List<String> roles;

    public LoginResponse(String username, String userId, List<String> roles, String token) {
        this.username = username;
//        this.roles = roles;
        this.userId = userId;
        this.token = token;
    }

    public LoginResponse(Map errors) {
        this.errors = errors;
    }

}
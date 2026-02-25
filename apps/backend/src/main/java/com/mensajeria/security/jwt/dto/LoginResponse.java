package com.mensajeria.security.jwt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class LoginResponse {

    private Map<String, Object> errors;
    private LoginData data;
//    private List<String> roles;

    public LoginResponse(String username, String userId, String token) {
        this.data = new LoginData(username, userId, token);
    }

    public LoginResponse(Map errors) {
        this.errors = errors;
    }

}
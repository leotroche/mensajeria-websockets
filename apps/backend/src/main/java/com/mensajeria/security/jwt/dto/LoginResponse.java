package com.mensajeria.security.jwt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class LoginResponse {

    private Map<String, Object> errors;
    private String secret;
    private String username;
    private List<String> roles;

    public LoginResponse(String username, List<String> roles, String secret) {
        this.username = username;
        this.roles = roles;
        this.secret = secret;
    }

    public LoginResponse(Map errors) {
        this.errors = errors;
    }

}
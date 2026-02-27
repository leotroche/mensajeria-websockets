package com.mensajeria.controller.dto.security;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Setter
@Getter
public class LoginResponse {

    private Map<String, Object> errors;
    private LoginData data;

    public LoginResponse(String username, String userId, String token) {
        this.data = new LoginData(token);
    }

    public LoginResponse(Map errors) {
        this.errors = errors;
    }

}
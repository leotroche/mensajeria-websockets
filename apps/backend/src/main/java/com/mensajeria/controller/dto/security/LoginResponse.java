package com.mensajeria.controller.dto.security;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Setter
@Getter
public class LoginResponse {

    private String error;
    private LoginData data;

    public LoginResponse(String token) {
        this.data = new LoginData(token);
    }

    public LoginResponse(Map error) {
        this.error = error.toString();
    }

}
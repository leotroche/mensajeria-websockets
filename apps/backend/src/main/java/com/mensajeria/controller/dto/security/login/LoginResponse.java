package com.mensajeria.controller.dto.security.login;

import com.mensajeria.controller.dto.security.LoginData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {

//    private String error;
    private LoginData data;

    public LoginResponse(String token) {
        this.data = new LoginData(token);
    }

//    public LoginResponse(Map error) {
//        this.error = error.toString();
//    }

}
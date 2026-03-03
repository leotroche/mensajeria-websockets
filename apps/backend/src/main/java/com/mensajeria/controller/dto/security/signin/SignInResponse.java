package com.mensajeria.controller.dto.security.signin;

import com.mensajeria.controller.dto.security.LoginData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignInResponse {

    private LoginData data;

    public SignInResponse(String token) {
        this.data = new LoginData(token);
    }

}
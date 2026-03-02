package com.mensajeria.service;

import com.mensajeria.controller.dto.security.LoginRequest;
import com.mensajeria.controller.dto.security.LoginResponse;
import com.mensajeria.controller.dto.userinfo.UserInfo;
import com.mensajeria.model.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // importante poner en TODOS los tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserInfoServiceTest {

    @Autowired
    UserInfoServiceImpl userInfoService;

    @Autowired
    SecurityServiceImpl securityService;

    @Test
    public void userInfoWillReturnNameAndId() { // TODO cambiar esto cuando se puedan hacer nuevos usuarios, para crearlo DENTRO del test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        UserInfo userInfo = userInfoService.getUserInfo(loginResponse.getData().token());

        assertEquals("pepe", userInfo.getData().username());
        assertEquals("1", userInfo.getData().userId());
    }

    @Test
    public void userInfoWillReturnErrorOnInvalidToken() {
        assertThrows(UserNotFoundException.class, () -> userInfoService.getUserInfo("xd"));

    }

}

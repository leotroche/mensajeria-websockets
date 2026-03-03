package com.mensajeria.service;

import com.mensajeria.controller.dto.security.login.LoginRequest;
import com.mensajeria.controller.dto.security.login.LoginResponse;
import com.mensajeria.controller.dto.userinfo.UserInfo;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.utils.TestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // importante poner en TODOS los tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserServiceTest {

    @Autowired
    TestService testService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    SecurityServiceImpl securityService;



    @BeforeEach
    public void setup() {
        testService.createTestUsers();

    }

    @Test
    public void userInfoWillReturnNameAndId() { // TODO cambiar esto cuando se puedan hacer nuevos usuarios, para crearlo DENTRO del test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        UserInfo userInfo = userService.getUserInfo(loginResponse.getData().token());

        assertEquals("pepe", userInfo.getData().username());
        assertEquals("1", userInfo.getData().userId());
    }

    @Test
    public void userInfoWillReturnErrorOnInvalidToken() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserInfo("xd"));

    }

    @Test
    public void createUserWithTheCorrespondingDetails() {

        userService.create("pepito", "1234");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepito");
        loginRequest.setPassword("1234");

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        UserInfo userInfo = userService.getUserInfo(loginResponse.getData().token());

        assertEquals("pepito", userInfo.getData().username());
        assertEquals("3", userInfo.getData().userId()); // TODO si este test falla, puede ser por el 3 hardcodeado como id
    }

    @Test
    public void createUserAndSignInWithAToken() {

        LoginResponse loginResponse = userService.create("pepito", "1234");

        UserInfo userInfo = userService.getUserInfo(loginResponse.getData().token());

        assertEquals("pepito", userInfo.getData().username());
        assertEquals("3", userInfo.getData().userId()); // TODO si este test falla, puede ser por el 3 hardcodeado como id
    }

    @AfterEach
    public void teardown() {
        testService.removeAllUsers();
    }




}

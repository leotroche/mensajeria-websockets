package com.mensajeria.service;

import com.mensajeria.controller.dto.security.login.LoginRequest;
import com.mensajeria.controller.dto.security.login.LoginResponse;
import com.mensajeria.model.exception.UserNotFoundException;
import com.mensajeria.utils.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // importante poner en TODOS los tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SecurityServiceTest {

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    TestService testService;


    @BeforeEach void setup() {
        testService.createTestUsers();
    }


    @Test
    public void loginWillReturnToken() { // TODO cambiar esto cuando se puedan hacer nuevos usuarios, para crearlo DENTRO del test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        assertNotNull(loginResponse.getData().token());

    }

    @Test
    public void loginWillReturnErrorWithInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("99999999999");

        assertThrows(UserNotFoundException.class, () -> securityService.authenticateUser(loginRequest));

    }

}

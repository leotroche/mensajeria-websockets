package com.mensajeria;

import com.mensajeria.security.jwt.dto.LoginRequest;
import com.mensajeria.security.jwt.dto.LoginResponse;
import com.mensajeria.service.SecurityServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test") // importante poner en TODOS los tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SecurityServiceTest {

    @Autowired
    SecurityServiceImpl securityService;


    @Test
    public void loginWillReturnUserID() { // TODO cambiar esto cuando se puedan hacer nuevos usuarios, para crearlo DENTRO del test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        assertEquals("1", loginResponse.getData().userId());

    }

    @Test
    public void loginWillReturnUsername() { // TODO cambiar esto cuando se puedan hacer nuevos usuarios, para crearlo DENTRO del test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        assertEquals("pepe", loginResponse.getData().username());

    }

    @Test
    public void loginWillReturnSecret() { // TODO cambiar esto cuando se puedan hacer nuevos usuarios, para crearlo DENTRO del test
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("pepe");
        loginRequest.setPassword("pepe1234");

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        assertNotNull(loginResponse.getData().token());

    }

}

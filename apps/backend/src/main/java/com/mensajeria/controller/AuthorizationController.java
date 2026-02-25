package com.mensajeria.controller;

import com.mensajeria.security.jwt.dto.LoginRequest;
import com.mensajeria.security.jwt.dto.LoginResponse;
import com.mensajeria.service.SecurityServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class AuthorizationController {

    SecurityServiceImpl securityService;

    public AuthorizationController(SecurityServiceImpl securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        boolean cantLogin = loginResponse.getErrors() != null;

        if (cantLogin) return new ResponseEntity<Object>(loginResponse.getErrors(), HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/sayhello")
    public ResponseEntity<?> hiThere() {
        return ResponseEntity.ok("Hola pepe");
    }
}

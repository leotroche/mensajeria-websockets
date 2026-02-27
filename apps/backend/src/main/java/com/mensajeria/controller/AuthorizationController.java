package com.mensajeria.controller;

import com.mensajeria.controller.dto.security.LoginRequest;
import com.mensajeria.controller.dto.security.LoginResponse;
import com.mensajeria.service.SecurityServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
//@RequestMapping("/api")
public class AuthorizationController {

    SecurityServiceImpl securityService;

    public AuthorizationController(SecurityServiceImpl securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        boolean cantLogin = loginResponse == null;

        if (cantLogin) return new ResponseEntity<Object>(loginResponse.getError(), HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/sayhello")
    public ResponseEntity<?> hiThere() {
        return ResponseEntity.ok("Hola pepe");
    }
}

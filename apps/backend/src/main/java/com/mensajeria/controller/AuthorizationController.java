package com.mensajeria.controller;

import com.mensajeria.controller.dto.security.login.LoginRequest;
import com.mensajeria.controller.dto.security.login.LoginResponse;
import com.mensajeria.controller.dto.security.signin.SignInRequest;
import com.mensajeria.service.SecurityServiceImpl;
import com.mensajeria.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class AuthorizationController {

    private final SecurityServiceImpl securityService;
    private final UserServiceImpl userService;

    public AuthorizationController(SecurityServiceImpl securityService, UserServiceImpl userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = securityService.authenticateUser(loginRequest);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> createUser(@RequestBody SignInRequest signInRequest) {

        LoginResponse loginResponse = userService.create(signInRequest.getUsername(), signInRequest.getPassword());

        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/sayhello")
    public ResponseEntity<?> hiThere() {
        return ResponseEntity.ok("Hola pepe");
    }
}

package com.mensajeria.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @RequestMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) { // TODO parentemente es para browsers nomás, así que hay que considerar sacarlo
        return token;
    }
}
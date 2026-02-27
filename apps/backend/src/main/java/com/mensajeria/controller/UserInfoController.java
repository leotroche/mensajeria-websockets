package com.mensajeria.controller;

import com.mensajeria.controller.dto.UserInfo;
import com.mensajeria.model.chat.Information;
import com.mensajeria.model.chat.Message;
import com.mensajeria.service.UserInfoServiceImpl;
import com.mensajeria.utils.JwtUtils;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserInfoController {

    private final JwtUtils jwtUtils;
    private final UserInfoServiceImpl userInfoService;

    public UserInfoController(JwtUtils jwtUtils, UserInfoServiceImpl userInfoService) {
        this.jwtUtils = jwtUtils;
        this.userInfoService = userInfoService;
    }

    @GetMapping("/me")
    public UserInfo getUserInfo(@RequestHeader("Authorization") String token) {
        String parsedToken = jwtUtils.validateAndCutToken(token);
        return userInfoService.getUserInfo(parsedToken);
    }

}

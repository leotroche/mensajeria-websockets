package com.mensajeria.controller;

import com.mensajeria.controller.dto.userinfo.UserInfo;
import com.mensajeria.service.UserInfoServiceImpl;
import com.mensajeria.utils.JwtUtils;
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

package com.mensajeria.controller.dto.userinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private String error;
    private final UserInfoData data;

    public UserInfo(UserInfoData data) {
        this.data = data;
    }

    public UserInfo(String error, UserInfoData data) {
        this.error = error;
        this.data = data;
    }
}

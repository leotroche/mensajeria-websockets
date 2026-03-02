package com.mensajeria.controller.dto.userinfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfo {
    private final UserInfoData data;

    public UserInfo(UserInfoData data) {
        this.data = data;
    }

}

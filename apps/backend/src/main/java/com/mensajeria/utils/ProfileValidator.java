package com.mensajeria.utils;

import org.springframework.beans.factory.annotation.Value;

public class ProfileValidator
{
    @Value("${spring.profiles.active:}") // TODO borrar en prod
    private String activeProfile;

    public boolean isTestProfileActive() {
        return "test".equals(activeProfile);
    }
}

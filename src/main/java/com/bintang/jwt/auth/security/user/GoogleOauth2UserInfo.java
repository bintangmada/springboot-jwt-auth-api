package com.bintang.jwt.auth.security.user;

import org.springframework.security.oauth2.core.user.OAuth2User;

public class GoogleOauth2UserInfo implements OAuth2UserInfo{

    private final OAuth2User user;

    public GoogleOauth2UserInfo(OAuth2User user){
        this.user = user;
    }
    @Override
    public String getEmail() {
        return user.getAttribute("email");
    }

    @Override
    public String getName() {
        return user.getAttribute("name");
    }

    @Override
    public String getProviderId() {
        return user.getName();
    }
}

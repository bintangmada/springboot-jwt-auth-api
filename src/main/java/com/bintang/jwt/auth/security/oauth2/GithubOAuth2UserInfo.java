package com.bintang.jwt.auth.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

public class GithubOAuth2UserInfo implements OAuth2UserInfo {

    private final OAuth2User user;

    public GithubOAuth2UserInfo(OAuth2User user) {
        this.user = user;
    }

    @Override
    public String getEmail() {
        return user.getAttribute("email"); // bisa null
    }

    @Override
    public String getName() {
        return user.getAttribute("login");
    }

    @Override
    public String getProviderId() {
        return String.valueOf(user.getAttribute("id"));
    }
}


package com.bintang.jwt.auth.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo get(String provider, OAuth2User user){
        return switch(provider){
            case "google" -> new GoogleOauth2UserInfo(user);
            case "github" -> new GithubOAuth2UserInfo(user);
            default -> throw new IllegalArgumentException("Unsupported provider");
        };
    }
}


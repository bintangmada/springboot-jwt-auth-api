package com.bintang.jwt.auth.security.oauth2;

public interface OAuth2UserInfo {

    String getEmail();
    String getName();
    String getProviderId();

}

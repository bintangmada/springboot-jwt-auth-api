package com.bintang.jwt.auth.security.user;

public interface OAuth2UserInfo {

    String getEmail();
    String getName();
    String getProviderId();

}

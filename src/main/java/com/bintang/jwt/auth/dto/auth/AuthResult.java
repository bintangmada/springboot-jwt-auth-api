package com.bintang.jwt.auth.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResult {

    private String accessToken;
    private String refreshToken;

}

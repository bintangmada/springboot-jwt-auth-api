package com.bintang.jwt.auth.dto.user;

import com.bintang.jwt.auth.entity.enums.AuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private AuthProvider provider;

}

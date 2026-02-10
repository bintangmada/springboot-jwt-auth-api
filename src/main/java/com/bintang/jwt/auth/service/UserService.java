package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.dto.user.UserResponse;

public interface UserService {
    UserResponse createUser(RegisterRequest request);

    void delete(Long id);

}

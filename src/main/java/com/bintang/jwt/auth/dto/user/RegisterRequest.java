package com.bintang.jwt.auth.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank(message = "Name required")
    private String name;

    @NotBlank(message = "Email required")
    @Email
    private String email;

    @NotBlank(message = "Password required")
    private String password;

}

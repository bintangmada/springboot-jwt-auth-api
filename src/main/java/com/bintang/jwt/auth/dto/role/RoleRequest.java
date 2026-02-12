package com.bintang.jwt.auth.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {

    @NotBlank(message = "Role name is required")
    private String name;

}
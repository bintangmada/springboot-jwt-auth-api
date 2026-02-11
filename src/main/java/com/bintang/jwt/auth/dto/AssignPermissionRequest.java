package com.bintang.jwt.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignPermissionRequest {

    @NotNull
    private Long roleId;

    @NotNull
    private Long permissionId;

}

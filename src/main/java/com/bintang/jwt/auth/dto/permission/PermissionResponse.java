package com.bintang.jwt.auth.dto.permission;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionResponse {

    private Long id;
    private String name;

}

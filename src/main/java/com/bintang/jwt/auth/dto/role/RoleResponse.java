package com.bintang.jwt.auth.dto.role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleResponse {

    private Long id;
    private String name;

}

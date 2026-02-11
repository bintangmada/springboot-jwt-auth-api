package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.RoleRequest;
import com.bintang.jwt.auth.dto.RoleResponse;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleResponse create(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = new Role();
        role.setName(request.getName());

        return mapToRoleResponse(roleRepository.save(role));
    }

    RoleResponse mapToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

}

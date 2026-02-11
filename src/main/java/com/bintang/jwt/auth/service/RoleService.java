package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.RoleRequest;
import com.bintang.jwt.auth.dto.RoleResponse;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    public RoleResponse create(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = new Role();
        role.setName(request.getName());

        return mapToRoleResponse(roleRepository.save(role));
    }

    public RoleResponse update (Long id, RoleRequest request){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(request.getName());

        return mapToRoleResponse(roleRepository.save(role));
    }

    public void delete(Long id){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setStatus(0L);
        role.setDeletedAt(LocalDateTime.now());
        role.setDeleted(true);
        role.setDeletedBy(auth != null ? auth.getName() : "SYSTEM");
    }

    RoleResponse mapToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

}

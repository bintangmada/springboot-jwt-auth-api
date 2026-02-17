package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.role.RoleRequest;
import com.bintang.jwt.auth.dto.role.RoleResponse;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    @Transactional
    public RoleResponse create(RoleRequest request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = new Role();
        role.setName(request.getName());
        Role savedRole = roleRepository.save(role);

        return mapToRoleResponse(savedRole);
    }

    public RoleResponse findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role is not found"));

        return mapToRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(this::mapToRoleResponse)
                .toList();
    }

    Page<RoleResponse> getAllRolesPageable(Pageable pageable){
        return roleRepository.findAll(pageable).map(this::mapToRoleResponse);
    }

    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(request.getName());

        return mapToRoleResponse(roleRepository.save(role));
    }

    public void delete(Long id) {
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

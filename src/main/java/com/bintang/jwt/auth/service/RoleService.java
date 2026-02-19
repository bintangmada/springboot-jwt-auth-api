package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.role.RoleRequest;
import com.bintang.jwt.auth.dto.role.RoleResponse;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
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

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    public RoleResponse create(RoleRequest request) {

        if (roleRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new ConflictException("Role already exists");
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setStatus(1L);
        role.setDeleted(false);

        return mapToRoleResponse(roleRepository.save(role));
    }
    public RoleResponse findById(Long id) {
        Role role = roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        return mapToRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToRoleResponse)
                .toList();
    }

    Page<RoleResponse> getAllRolesPageable(Pageable pageable){
        return roleRepository.findAll(pageable).map(this::mapToRoleResponse);
    }

    public RoleResponse update(Long id, RoleRequest request) {

        Role role = roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (!role.getName().equals(request.getName())
                && roleRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new ConflictException("Role name already used");
        }

        role.setName(request.getName());

        return mapToRoleResponse(roleRepository.save(role));
    }

    public void delete(Long id) {

        Role role = roleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        role.setStatus(0L);
        role.setDeleted(true);
        role.setDeletedAt(LocalDateTime.now());
        role.setDeletedBy(currentUser());

        roleRepository.save(role);
    }

    private RoleResponse mapToRoleResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

}

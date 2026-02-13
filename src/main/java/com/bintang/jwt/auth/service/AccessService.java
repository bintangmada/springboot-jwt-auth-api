package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.entity.RolePermission;
import com.bintang.jwt.auth.entity.UserPermission;
import com.bintang.jwt.auth.entity.UserRole;
import com.bintang.jwt.auth.repository.mapping.RolePermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserPermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    private final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    public void assignRoleToUser(Long userId, Long roleId) {
        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            throw new RuntimeException("Role already assigned");
        }

        UserRole mapping = new UserRole();
        mapping.setUserId(userId);
        mapping.setRoleId(roleId);

        userRoleRepository.save(mapping);
    }

    public void revokeRoleFromUser(Long userId, Long roleId) {

        if (!userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            throw new RuntimeException("Role is already revoked from this user");
        }

        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId);
        userRole.setStatus(0L);
        userRole.setDeleted(true);
        userRole.setDeletedAt(LocalDateTime.now());
        userRole.setDeletedBy(auth != null ? auth.getName() : "SYSTEM");

        userRoleRepository.save(userRole);

    }

    public void assignPermissionToRole(Long roleId, Long permissionId) {
        if (rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId)) {
            throw new RuntimeException("Permission already assigned to role");
        }

        RolePermission mapping = new RolePermission();
        mapping.setRoleId(roleId);
        mapping.setPermissionId(permissionId);

        rolePermissionRepository.save(mapping);
    }

    public void revokePermissionFromRole(Long roleId, Long permissionId) {
        RolePermission rolePermission = rolePermissionRepository.findByRoleIdAndPermissionId(roleId, permissionId);

        if(rolePermission == null || rolePermission.isDeleted()){
            throw new RuntimeException("Permission is already revoked from this role");
        }

        trackingSoftDeleteRolePermission(rolePermission);

        rolePermissionRepository.save(rolePermission);

    }

    public void assignPermissionToUser(Long userId, Long permissionId){
        if(userPermissionRepository.existsByUserIdAndPermissionId(userId, permissionId)){
            throw new RuntimeException("Permission already assigned to user");
        }

        UserPermission mapping = new UserPermission();
        mapping.setUserId(userId);
        mapping.setPermissionId(permissionId);

        userPermissionRepository.save(mapping);
    }

    public void revokePermissionFromUser(Long userId, Long permissionId){
        UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionId(userId, permissionId);

        if(userPermission == null || userPermission.isDeleted()){
            throw new RuntimeException("Permission is already revoked from this user");
        }

        userPermission.setStatus(0L);
        userPermission.setDeleted(true);
        userPermission.setDeletedAt(LocalDateTime.now());
        userPermission.setDeletedBy(auth != null ? auth.getName() : "SYSTEM");

        userPermissionRepository.save(userPermission);
    }

    public void trackingSoftDeleteRolePermission(RolePermission rolePermission) {
        rolePermission.setStatus(0L);
        rolePermission.setDeleted(true);
        rolePermission.setDeletedAt(LocalDateTime.now());
        rolePermission.setDeletedBy(auth != null ? auth.getName() : "SYSTEM");
    }
}

package com.bintang.jwt.auth.service;

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

    public void assignRoleToUser(Long userId, Long roleId){
        if(userRoleRepository.existsByUserIdAndRoleId(userId, roleId)){
            throw new RuntimeException("Role already assigned");
        }

        UserRole mapping = new UserRole();
        mapping.setUserId(userId);
        mapping.setRoleId(roleId);

        userRoleRepository.save(mapping);
    }

    public void revokeRoleFromUser(Long userId, Long roleId){

        if(!userRoleRepository.existsByUserIdAndRoleId(userId, roleId)){
            throw new RuntimeException("Role is already revoke from this user");
        }

        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId);
        userRole.setStatus(0L);
        userRole.setDeleted(true);
        userRole.setDeletedAt(LocalDateTime.now());
        userRole.setDeletedBy(auth != null ? auth.getName() : "SYSTEM");

        userRoleRepository.save(userRole);

    }
}

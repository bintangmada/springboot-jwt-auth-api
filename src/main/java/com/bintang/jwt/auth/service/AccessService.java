package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.entity.UserRole;
import com.bintang.jwt.auth.repository.mapping.RolePermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserPermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessService {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    public void assignRoleToUser(Long userId, Long roleId){
        if(userRoleRepository.existsByUserIdAndRoleId(userId, roleId)){
            throw new RuntimeException("Role already assigned");
        }

        UserRole mapping = new UserRole();
        mapping.setUserId(userId);
        mapping.setRoleId(roleId);

        userRoleRepository.save(mapping);
    }
}

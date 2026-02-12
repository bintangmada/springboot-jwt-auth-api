package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.permission.PermissionRequest;
import com.bintang.jwt.auth.dto.permission.PermissionResponse;
import com.bintang.jwt.auth.entity.Permission;
import com.bintang.jwt.auth.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionResponse create (PermissionRequest request){
        if(permissionRepository.existsByName(request.getName())){
            throw new RuntimeException("Permission already exists");
        }

        Permission permission = new Permission();
        permission.setName(request.getName());
        return mapToResponse(permissionRepository.save(permission));
    }

    PermissionResponse mapToResponse(Permission permission){
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .build();
    }

}

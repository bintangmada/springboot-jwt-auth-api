package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.permission.PermissionRequest;
import com.bintang.jwt.auth.dto.permission.PermissionResponse;
import com.bintang.jwt.auth.entity.Permission;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private String currentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    public PermissionResponse create (PermissionRequest request){
        if(permissionRepository.existsByName(request.getName())){
            throw new ConflictException("Permission already exists");
        }

        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setStatus(1L);
        permission.setDeleted(false);
        return mapToResponse(permissionRepository.save(permission));
    }

    public PermissionResponse update(Long id, PermissionRequest request){
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        if (!permission.getName().equals(request.getName())
                && permissionRepository.existsByNameAndIsDeletedFalse(request.getName())) {
            throw new ConflictException("Permission name already used");
        }

        permission.setName(request.getName());

        return mapToResponse(permissionRepository.save(permission));
    }

    public void delete(Long id){
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        permission.setDeletedAt(LocalDateTime.now());
        permission.setDeleted(true);
        permission.setDeletedBy(currentUser());
        permission.setStatus(0L);

        permissionRepository.save(permission);

    }

    PermissionResponse mapToResponse(Permission permission){
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .build();
    }

}

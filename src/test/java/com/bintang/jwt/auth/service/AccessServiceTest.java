package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.entity.RolePermission;
import com.bintang.jwt.auth.entity.UserPermission;
import com.bintang.jwt.auth.entity.UserRole;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.mapping.RolePermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserPermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @InjectMocks
    private AccessService accessService;

    @Test
    void assignRoleToUser_WithNewMapping_ShouldSaveUserRole() {
        Mockito.when(userRoleRepository.existsByUserIdAndRoleId(1L, 2L)).thenReturn(false);

        accessService.assignRoleToUser(1L, 2L);

        Mockito.verify(userRoleRepository).save(any(UserRole.class));
    }

    @Test
    void assignRoleToUser_WithExistingMapping_ShouldThrowConflict() {
        Mockito.when(userRoleRepository.existsByUserIdAndRoleId(1L, 2L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> accessService.assignRoleToUser(1L, 2L));
    }

    @Test
    void revokeRoleFromUser_WithValidMapping_ShouldSoftDelete() {
        UserRole mapping = new UserRole();
        mapping.setUserId(1L);
        mapping.setRoleId(2L);
        mapping.setDeleted(false);

        Mockito.when(userRoleRepository.existsByUserIdAndRoleId(1L, 2L)).thenReturn(true);
        Mockito.when(userRoleRepository.findByUserIdAndRoleId(1L, 2L)).thenReturn(mapping);

        accessService.revokeRoleFromUser(1L, 2L);

        assertTrue(mapping.isDeleted());
        Mockito.verify(userRoleRepository).save(mapping);
    }

    @Test
    void assignPermissionToRole_ShouldSaveMapping() {
        Mockito.when(rolePermissionRepository.existsByRoleIdAndPermissionId(1L, 3L)).thenReturn(false);

        accessService.assignPermissionToRole(1L, 3L);

        Mockito.verify(rolePermissionRepository).save(any(RolePermission.class));
    }

    @Test
    void revokePermissionFromRole_ShouldSoftDeleteMapping() {
        RolePermission mapping = new RolePermission();
        mapping.setRoleId(1L);
        mapping.setPermissionId(3L);
        mapping.setDeleted(false);

        Mockito.when(rolePermissionRepository.findByRoleIdAndPermissionId(1L, 3L)).thenReturn(mapping);

        accessService.revokePermissionFromRole(1L, 3L);

        assertTrue(mapping.isDeleted());
        Mockito.verify(rolePermissionRepository).save(mapping);
    }

    @Test
    void assignPermissionToUser_ShouldSaveMapping() {
        Mockito.when(userPermissionRepository.existsByUserIdAndPermissionId(1L, 4L)).thenReturn(false);

        accessService.assignPermissionToUser(1L, 4L);

        Mockito.verify(userPermissionRepository).save(any(UserPermission.class));
    }

    @Test
    void revokePermissionFromUser_ShouldSoftDeleteMapping() {
        UserPermission mapping = new UserPermission();
        mapping.setUserId(1L);
        mapping.setPermissionId(4L);
        mapping.setDeleted(false);

        Mockito.when(userPermissionRepository.findByUserIdAndPermissionId(1L, 4L)).thenReturn(mapping);

        accessService.revokePermissionFromUser(1L, 4L);

        assertTrue(mapping.isDeleted());
        Mockito.verify(userPermissionRepository).save(mapping);
    }
}

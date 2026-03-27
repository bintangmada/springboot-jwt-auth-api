package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.permission.PermissionRequest;
import com.bintang.jwt.auth.dto.permission.PermissionResponse;
import com.bintang.jwt.auth.entity.Permission;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission mockPerm;

    @BeforeEach
    void setUp() {
        mockPerm = new Permission();
        mockPerm.setId(100L);
        mockPerm.setName("READ_DATA");
        mockPerm.setDeleted(false);
    }

    @Test
    void create_WithNewName_ShouldReturnResponse() {
        PermissionRequest request = new PermissionRequest();
        request.setName("WRITE_DATA");

        Mockito.when(permissionRepository.existsByName("WRITE_DATA")).thenReturn(false);
        Mockito.when(permissionRepository.save(any(Permission.class))).thenAnswer(i -> {
            Permission saved = i.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        PermissionResponse response = permissionService.create(request);

        assertNotNull(response);
        assertEquals("WRITE_DATA", response.getName());
    }

    @Test
    void create_WithExistingName_ShouldThrowException() {
        PermissionRequest request = new PermissionRequest();
        request.setName("READ_DATA");

        Mockito.when(permissionRepository.existsByName("READ_DATA")).thenReturn(true);

        assertThrows(ConflictException.class, () -> permissionService.create(request));
    }

    @Test
    void update_WithValidRequest_ShouldUpdateName() {
        PermissionRequest request = new PermissionRequest();
        request.setName("READ_WRITE_DATA");

        Mockito.when(permissionRepository.findById(100L)).thenReturn(Optional.of(mockPerm));
        Mockito.when(permissionRepository.existsByNameAndIsDeletedFalse("READ_WRITE_DATA")).thenReturn(false);
        Mockito.when(permissionRepository.save(any(Permission.class))).thenReturn(mockPerm);

        PermissionResponse response = permissionService.update(100L, request);

        assertEquals("READ_WRITE_DATA", response.getName());
    }

    @Test
    void delete_WithValidId_ShouldSoftDelete() {
        Mockito.when(permissionRepository.findById(100L)).thenReturn(Optional.of(mockPerm));

        permissionService.delete(100L);

        assertTrue(mockPerm.isDeleted());
        Mockito.verify(permissionRepository).save(mockPerm);
    }
}

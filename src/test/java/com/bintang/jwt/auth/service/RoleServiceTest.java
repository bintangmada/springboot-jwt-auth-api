package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.role.RoleRequest;
import com.bintang.jwt.auth.dto.role.RoleResponse;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role mockRole;

    @BeforeEach
    void setUp() {
        mockRole = new Role();
        mockRole.setId(10L);
        mockRole.setName("USER");
        mockRole.setDeleted(false);
    }

    @Test
    void create_WithNewRole_ShouldReturnResponse() {
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");

        Mockito.when(roleRepository.existsByNameAndIsDeletedFalse("ADMIN")).thenReturn(false);
        Mockito.when(roleRepository.save(any(Role.class))).thenAnswer(i -> {
            Role saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        RoleResponse response = roleService.create(request);

        assertNotNull(response);
        assertEquals("ADMIN", response.getName());
    }

    @Test
    void create_WithExistingRole_ShouldThrowConflictException() {
        RoleRequest request = new RoleRequest();
        request.setName("USER");

        Mockito.when(roleRepository.existsByNameAndIsDeletedFalse("USER")).thenReturn(true);

        assertThrows(ConflictException.class, () -> roleService.create(request));
    }

    @Test
    void findById_WithValidId_ShouldReturnResponse() {
        Mockito.when(roleRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(mockRole));

        RoleResponse response = roleService.findById(10L);
        assertEquals("USER", response.getName());
    }

    @Test
    void getAll_ShouldReturnList() {
        Mockito.when(roleRepository.findAllByIsDeletedFalse()).thenReturn(List.of(mockRole));

        List<RoleResponse> responses = roleService.getAll();
        assertEquals(1, responses.size());
        assertEquals("USER", responses.get(0).getName());
    }

    @Test
    void getAllRolesPageable_ShouldReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Mockito.when(roleRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(mockRole)));

        Page<RoleResponse> page = roleService.getAllRolesPageable(pageRequest);
        assertEquals(1, page.getTotalElements());
    }

    @Test
    void update_WithValidRequest_ShouldUpdateAndSave() {
        RoleRequest request = new RoleRequest();
        request.setName("SUPER_USER");

        Mockito.when(roleRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(mockRole));
        Mockito.when(roleRepository.existsByNameAndIsDeletedFalse("SUPER_USER")).thenReturn(false);
        Mockito.when(roleRepository.save(any(Role.class))).thenReturn(mockRole);

        RoleResponse response = roleService.update(10L, request);

        assertEquals("SUPER_USER", response.getName());
    }

    @Test
    void delete_WithValidId_ShouldSoftDelete() {
        Mockito.when(roleRepository.findByIdAndIsDeletedFalse(10L)).thenReturn(Optional.of(mockRole));

        roleService.delete(10L);

        assertTrue(mockRole.isDeleted());
        assertEquals("SYSTEM", mockRole.getDeletedBy());
        Mockito.verify(roleRepository).save(mockRole);
    }
}

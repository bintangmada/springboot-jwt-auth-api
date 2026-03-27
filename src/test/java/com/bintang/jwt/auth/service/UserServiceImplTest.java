package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.dto.user.UpdateUserRequest;
import com.bintang.jwt.auth.dto.user.UserResponse;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Bintang");
        mockUser.setEmail("bintang@example.com");
        mockUser.setPassword("encoded_password");
        mockUser.setDeleted(false);
    }

    @Test
    void create_WithNewEmail_ShouldReturnUserResponse() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Bintang");
        request.setEmail("new@example.com");
        request.setPassword("plain_password");

        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("plain_password")).thenReturn("encoded_password");
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User saved = i.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        // Act
        UserResponse response = userService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals("Bintang", response.getName());
        assertEquals("new@example.com", response.getEmail());
        assertEquals("LOCAL", response.getProvider());
    }

    @Test
    void create_WithExistingEmail_ShouldThrowConflictException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("bintang@example.com");

        Mockito.when(userRepository.findByEmail("bintang@example.com"))
               .thenReturn(Optional.of(mockUser));

        assertThrows(ConflictException.class, () -> userService.create(request));
    }

    @Test
    void getById_WithValidId_ShouldReturnUserResponse() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        
        UserResponse response = userService.getById(1L);
        assertNotNull(response);
        assertEquals("Bintang", response.getName());
    }

    @Test
    void getById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void getAll_ShouldReturnListOfUserResponses() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(mockUser));
        
        List<UserResponse> responses = userService.getAll();
        assertEquals(1, responses.size());
        assertEquals("Bintang", responses.get(0).getName());
    }

    @Test
    void update_WithValidRequest_ShouldUpdateAndReturnResponse() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Bintang Update");
        request.setEmail("update@example.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.when(userRepository.existsByEmailAndIsDeletedFalse("update@example.com")).thenReturn(false);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponse response = userService.update(1L, request);

        // Assert
        assertEquals("Bintang Update", response.getName());
        assertEquals("update@example.com", response.getEmail());
    }

    @Test
    void update_WithExistingEmail_ShouldThrowConflictException() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("other@example.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.when(userRepository.existsByEmailAndIsDeletedFalse("other@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(1L, request));
    }

    @Test
    void delete_WithValidId_ShouldSoftDeleteUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        
        userService.delete(1L);

        assertTrue(mockUser.isDeleted());
        assertEquals("SYSTEM", mockUser.getDeletedBy());
        Mockito.verify(userRepository).save(mockUser);
    }
}

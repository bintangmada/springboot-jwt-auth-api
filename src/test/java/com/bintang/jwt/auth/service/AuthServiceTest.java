package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.auth.AuthResult;
import com.bintang.jwt.auth.dto.auth.LoginRequest;
import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.entity.RefreshToken;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.exception.BadRequestException;
import com.bintang.jwt.auth.exception.ConflictException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.RoleRepository;
import com.bintang.jwt.auth.repository.UserRepository;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AccessService accessService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResult() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("user@example.com");
        user.setAuthProvider("LOCAL");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(request.getEmail());
        
        when(jwtUtil.generateToken(userDetails)).thenReturn("mock-access-token");
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("mock-refresh-token");
        when(refreshTokenService.createRefreshToken(anyString())).thenReturn(refreshToken);

        // Act
        AuthResult result = authService.login(request);

        // Assert
        assertNotNull(result);
        assertEquals("mock-access-token", result.getAccessToken());
        assertEquals("mock-refresh-token", result.getRefreshToken());
    }

    @Test
    void login_WithGoogleUser_ShouldThrowBadRequestException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setEmail("user@example.com");
        user.setAuthProvider("GOOGLE"); // User terdaftar menggunakan Google

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.login(request));
        assertTrue(exception.getMessage().contains("Please login using GOOGLE"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowBadRequestException() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong-password");

        User user = new User();
        user.setEmail("user@example.com");
        user.setAuthProvider("LOCAL");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.login(request));
        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void login_WithUnregisteredEmail_ShouldThrowResourceNotFoundException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void register_WithValidNewEmail_ShouldSaveUserAndAssignRole() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("New User");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Role defaultRole = new Role();
        defaultRole.setId(2L);
        defaultRole.setName("ROLE_USER");
        when(roleRepository.findByNameAndIsDeletedFalse("ROLE_USER")).thenReturn(defaultRole);

        // Act
        authService.register(request);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByNameAndIsDeletedFalse("ROLE_USER");
        verify(accessService, times(1)).assignRoleToUser(1L, 2L);
    }

    @Test
    void register_WithExistingEmail_ShouldThrowConflictException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        User existingUser = new User();
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        ConflictException exception = assertThrows(ConflictException.class, () -> authService.register(request));
        assertEquals("Email already registered", exception.getMessage());
        
        // Pastikan tidak ada aksi penyimpanan karena gagal
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WhenDefaultRoleNotFound_ShouldCreateAndSaveDefaultRole() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("New User");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(10L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Disimulasikan Role belum ada di DB
        when(roleRepository.findByNameAndIsDeletedFalse("ROLE_USER")).thenReturn(null);

        Role newlyCreatedRole = new Role();
        newlyCreatedRole.setId(99L);
        when(roleRepository.save(any(Role.class))).thenReturn(newlyCreatedRole);

        // Act
        authService.register(request);

        // Assert
        // Pastikan role baru disimpan karena tidak ditemukan
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(accessService, times(1)).assignRoleToUser(10L, 99L);
    }
}

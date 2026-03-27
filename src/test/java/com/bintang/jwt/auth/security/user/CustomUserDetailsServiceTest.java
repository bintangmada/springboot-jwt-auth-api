package com.bintang.jwt.auth.security.user;

import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.repository.UserRepository;
import com.bintang.jwt.auth.repository.mapping.RolePermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserPermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private RolePermissionRepository rolePermissionRepository;
    @Mock
    private UserPermissionRepository userPermissionRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password");
    }

    private void mockUserDetailsBuilding() {
        when(userRoleRepository.findRoleNamesByUserId(mockUser.getId())).thenReturn(List.of("ROLE_USER"));
        when(userRoleRepository.findRoleIdsByUserId(mockUser.getId())).thenReturn(List.of(10L));
        when(rolePermissionRepository.findPermissionNamesByRoleIds(Set.of(10L))).thenReturn(List.of("READ_PRIVILEGE"));
        when(userPermissionRepository.findPermissionNamesByUserId(mockUser.getId())).thenReturn(List.of("SPECIAL_PRIVILEGE"));
    }

    @Test
    void loadUserByUsername_WithValidEmail_ShouldReturnCustomUserDetails() {
        // Arrange
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        mockUserDetailsBuilding();

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(mockUser.getEmail());

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        
        assertEquals(mockUser.getEmail(), customUserDetails.getUsername());
        var authorities = customUserDetails.getAuthorities();
        
        // 1 Role ("ROLE_USER" prepended to "ROLE_ROLE_USER") + 2 Permissions = 3 Authorities total
        assertEquals(3, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ROLE_USER")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("READ_PRIVILEGE")));
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("SPECIAL_PRIVILEGE")));
    }

    @Test
    void loadUserByUsername_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserByUsername("notfound@example.com")
        );
    }

    @Test
    void loadUserById_WithValidId_ShouldReturnCustomUserDetails() {
        // Arrange
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        mockUserDetailsBuilding();

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserById(mockUser.getId());

        // Assert
        assertNotNull(userDetails);
        assertEquals(mockUser.getEmail(), userDetails.getUsername());
    }

    @Test
    void loadUserById_WithInvalidId_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> 
            customUserDetailsService.loadUserById(999L)
        );
    }
}

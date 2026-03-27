package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.entity.RefreshToken;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.exception.BadRequestException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.RefreshTokenRepository;
import com.bintang.jwt.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User mockUser;
    private RefreshToken mockToken;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("user@example.com");

        mockToken = new RefreshToken();
        mockToken.setUser(mockUser);
        mockToken.setToken("dummy-refresh-token");
        mockToken.setExpiryDate(Instant.now().plusSeconds(3600)); // Berlaku 1 jam
        mockToken.setDeleted(false);
    }

    @Test
    void createRefreshToken_WithValidEmail_ShouldReturnNewToken() {
        // Arrange
        Mockito.when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        Mockito.when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(mockUser.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result.getUser());
        assertFalse(result.isDeleted());
        assertNotNull(result.getToken());
        
        // Memastikan token lama dihapus dulu sebelum membuat yang baru
        Mockito.verify(refreshTokenRepository).deleteByUserId(mockUser.getId());
        Mockito.verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_WithInvalidEmail_ShouldThrowResourceNotFound() {
        Mockito.when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            refreshTokenService.createRefreshToken("ghost@example.com")
        );
    }

    @Test
    void verifyExpiration_WithValidToken_ShouldReturnToken() {
        RefreshToken result = refreshTokenService.verifyExpiration(mockToken);
        assertEquals(mockToken, result);
    }

    @Test
    void verifyExpiration_WithExpiredToken_ShouldSoftDeleteAndThrowException() {
        // Arrange
        mockToken.setExpiryDate(Instant.now().minusSeconds(3600)); // Sudah expire 1 jam lalu

        // Act & Assert
        assertThrows(BadRequestException.class, () -> 
            refreshTokenService.verifyExpiration(mockToken)
        );

        assertTrue(mockToken.isDeleted());
        Mockito.verify(refreshTokenRepository).save(mockToken);
    }

    @Test
    void verifyExpiration_WithSoftDeletedToken_ShouldThrowException() {
        mockToken.setDeleted(true);

        assertThrows(BadRequestException.class, () -> 
            refreshTokenService.verifyExpiration(mockToken)
        );
    }

    @Test
    void findByToken_WithValidTokenString_ShouldReturnTokenEntity() {
        Mockito.when(refreshTokenRepository.findByTokenAndIsDeletedFalse(mockToken.getToken()))
               .thenReturn(Optional.of(mockToken));

        RefreshToken result = refreshTokenService.findByToken(mockToken.getToken());
        
        assertNotNull(result);
        assertEquals(mockToken.getToken(), result.getToken());
    }

    @Test
    void findByToken_WithInvalidTokenString_ShouldThrowException() {
        Mockito.when(refreshTokenRepository.findByTokenAndIsDeletedFalse("invalid-token"))
               .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            refreshTokenService.findByToken("invalid-token")
        );
    }

    @Test
    void findByToken_WithBlankTokenString_ShouldThrowBadRequest() {
        assertThrows(BadRequestException.class, () -> 
            refreshTokenService.findByToken("   ")
        );
    }
}

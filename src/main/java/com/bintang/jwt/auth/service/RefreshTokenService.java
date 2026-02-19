package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.entity.RefreshToken;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.exception.BadRequestException;
import com.bintang.jwt.auth.exception.ResourceNotFoundException;
import com.bintang.jwt.auth.repository.RefreshTokenRepository;
import com.bintang.jwt.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final long refreshTokenDurationMs = 7 * 24 * 60 * 60 * 1000;

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    public RefreshToken createRefreshToken(String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));

        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        token.setDeleted(false);
        token.setStatus(1L);

        return refreshTokenRepository.save(token);

    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            softDelete(token);
            throw new BadRequestException("Refresh token expired");
        }

        if (token.isDeleted()) {
            throw new BadRequestException("Refresh token is invalid");
        }

        return token;
    }

    public RefreshToken findByToken(String token) {

        if (token == null || token.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        return refreshTokenRepository
                .findByTokenAndIsDeletedFalse(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
    }

    public void delete(String token) {

        if (token == null || token.isBlank()) {
            return; // idempotent logout
        }

        refreshTokenRepository
                .findByTokenAndIsDeletedFalse(token)
                .ifPresent(this::softDelete);
    }

    private void softDelete(RefreshToken token) {
        token.setDeleted(true);
        token.setDeletedAt(LocalDateTime.now());
        token.setDeletedBy(currentUser());
        token.setStatus(0L);
        refreshTokenRepository.save(token);
    }

}

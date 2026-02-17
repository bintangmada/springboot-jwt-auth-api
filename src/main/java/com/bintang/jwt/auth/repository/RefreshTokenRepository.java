package com.bintang.jwt.auth.repository;

import com.bintang.jwt.auth.entity.RefreshToken;
import com.bintang.jwt.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenAndDeletedFalse(String token);

    void deleteByUserId(Long userId);

}

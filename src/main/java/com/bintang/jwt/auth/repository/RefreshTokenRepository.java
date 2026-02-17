package com.bintang.jwt.auth.repository;

import com.bintang.jwt.auth.entity.RefreshToken;
import com.bintang.jwt.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query(value = "SELECT * FROM refresh_token WHERE is_deleted = false", nativeQuery = true)
    Optional<RefreshToken> findByTokenAndIsDeletedFalse(String token);

    void deleteByUserId(Long userId);

}

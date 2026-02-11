package com.bintang.jwt.auth.repository.mapping;

import com.bintang.jwt.auth.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    List<UserPermission> findByUserId(Long userId);
    void deleteByUserIdAndPermissionId(Long userId, Long permissionId);
    boolean existsByUserIdAndPermissionId(Long userId, Long permissionId);
}

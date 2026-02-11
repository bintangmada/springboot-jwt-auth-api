package com.bintang.jwt.auth.repository.mapping;

import com.bintang.jwt.auth.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    List<UserPermission> findByUserId(Long userId);
    void deleteByUserIdAndPermissionId(Long userId, Long permissionId);
    boolean existsByUserIdAndPermissionId(Long userId, Long permissionId);

    @Query(value = "SELECT p.name FROM permissions p JOIN user_permission up ON up.permission_id = p.id WHERE up.user_id = :userId", nativeQuery = true)
    List<String> findPermissionNamesByUserId(Long userId);
}

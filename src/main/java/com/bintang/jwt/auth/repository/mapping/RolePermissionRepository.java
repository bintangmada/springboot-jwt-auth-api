package com.bintang.jwt.auth.repository.mapping;

import com.bintang.jwt.auth.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(Long roleId);
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
}

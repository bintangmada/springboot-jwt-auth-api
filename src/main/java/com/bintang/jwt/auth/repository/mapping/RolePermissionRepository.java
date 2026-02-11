package com.bintang.jwt.auth.repository.mapping;

import com.bintang.jwt.auth.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByRoleId(Long roleId);
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @Query(value = "SELECT DISTINCT p.name FROM permissions p JOIN role_permission rp ON rp.permission_id = p.id JOIN user_roles ur ON ur.role_id = rp_role_id WHERE ur_user_id = :userId", nativeQuery = true)
    List<String> findPermissionNamesByUserId(@Param("userId") Long userId);
}

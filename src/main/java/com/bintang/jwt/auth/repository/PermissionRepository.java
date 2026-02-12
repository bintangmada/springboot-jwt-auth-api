package com.bintang.jwt.auth.repository;

import com.bintang.jwt.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByName(String name);

}

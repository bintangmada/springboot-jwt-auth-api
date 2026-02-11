package com.bintang.jwt.auth.repository;

import com.bintang.jwt.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);

}

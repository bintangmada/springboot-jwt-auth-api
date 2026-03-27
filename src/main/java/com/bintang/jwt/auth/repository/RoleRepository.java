package com.bintang.jwt.auth.repository;

import com.bintang.jwt.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    boolean existsByName(String name);

    Role findByNameAndIsDeletedFalse(String name);

    boolean existsByNameAndIsDeletedFalse(String name);

    Optional<Role> findByIdAndIsDeletedFalse(Long id);

    List<Role> findAllByIsDeletedFalse();
}

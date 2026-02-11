package com.bintang.jwt.auth.repository.mapping;

import com.bintang.jwt.auth.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(Long userId);
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

}

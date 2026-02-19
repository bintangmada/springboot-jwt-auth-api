package com.bintang.jwt.auth.config;

import com.bintang.jwt.auth.entity.Permission;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.entity.RolePermission;
import com.bintang.jwt.auth.repository.PermissionRepository;
import com.bintang.jwt.auth.repository.RoleRepository;
import com.bintang.jwt.auth.repository.mapping.RolePermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepo,
                               PermissionRepository permRepo,
                               RolePermissionRepository rolePermRepo) {
        return args -> {

            Permission readProfile = permRepo.findByName("READ_PROFILE");
            if (readProfile == null) {
                readProfile = permRepo.save(
                        Permission.builder()
                                .name("READ_PROFILE")
                                .isDeleted(false)
                                .status(1L)
                                .build()
                );
            }

            Role userRole = roleRepo.findByNameAndIsDeletedFalse("ROLE_USER");
            if (userRole == null) {
                userRole = roleRepo.save(
                        Role.builder()
                                .name("ROLE_USER")
                                .status(1L)
                                .isDeleted(false)
                                .build()
                );
            }

            // assign permission to role if not exists yet
            if (!rolePermRepo.existsByRoleIdAndPermissionId(userRole.getId(), readProfile.getId())) {

                RolePermission rolePermission = RolePermission.builder()
                        .roleId(userRole.getId())
                        .permissionId(readProfile.getId())
                        .status(1L)
                        .isDeleted(false)
                        .build();

                rolePermRepo.save(rolePermission);
            }
        };
    }

}

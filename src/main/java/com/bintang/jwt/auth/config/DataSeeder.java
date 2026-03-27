package com.bintang.jwt.auth.config;

import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.entity.UserRole;
import com.bintang.jwt.auth.entity.enums.AuthProvider;
import com.bintang.jwt.auth.repository.RoleRepository;
import com.bintang.jwt.auth.repository.UserRepository;
import com.bintang.jwt.auth.repository.mapping.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and seeding database...");

        // 1. Seed Roles
        seedRole("ROLE_ADMIN");
        seedRole("ROLE_USER");

        // 2. Seed Admin User
        seedAdminUser("admin@example.com", "Admin Ganteng", "admin123");
    }

    private void seedRole(String roleName) {
        if (!roleRepository.existsByNameAndIsDeletedFalse(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .isDeleted(false)
                    .build();
            roleRepository.save(role);
            log.info("Seeded Role: {}", roleName);
        }
    }

    private void seedAdminUser(String email, String name, String password) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User admin = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .authProvider(AuthProvider.LOCAL.name())
                    .isDeleted(false)
                    .build();
            userRepository.save(admin);

            Role adminRole = roleRepository.findByNameAndIsDeletedFalse("ROLE_ADMIN");

            UserRole userRole = UserRole.builder()
                    .userId(admin.getId())
                    .roleId(adminRole.getId())
                    .isDeleted(false)
                    .build();
            userRoleRepository.save(userRole);

            log.info("Seeded Admin User: {} with password: {}", email, password);
        }
    }
}

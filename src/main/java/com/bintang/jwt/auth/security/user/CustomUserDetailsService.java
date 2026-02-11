package com.bintang.jwt.auth.security.user;

import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.repository.UserRepository;
import com.bintang.jwt.auth.repository.mapping.RolePermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserPermissionRepository;
import com.bintang.jwt.auth.repository.mapping.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return buildUserDetails(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return buildUserDetails(user);
    }

    private CustomUserDetails buildUserDetails(User user) {

        Set<String> roles = new HashSet<>(userRoleRepository.findRoleNamesByUserId(user.getId()));
        Set<String> rolePermissions = new HashSet<>(rolePermissionRepository.findPermissionNamesByUserId(user.getId()));
        Set<String> directPermissions = new HashSet<>(userPermissionRepository.findPermissionNamesByUserId(user.getId()));

        Set<String> finalPermissions = new HashSet<>();
        finalPermissions.addAll(rolePermissions);
        finalPermissions.addAll(directPermissions);

        return new CustomUserDetails(
                user,
                roles,
                finalPermissions
        );
    }


}

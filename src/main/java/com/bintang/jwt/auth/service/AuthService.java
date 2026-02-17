package com.bintang.jwt.auth.service;

import com.bintang.jwt.auth.dto.auth.AuthResponse;
import com.bintang.jwt.auth.dto.auth.AuthResult;
import com.bintang.jwt.auth.dto.auth.LoginRequest;
import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.entity.RefreshToken;
import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.entity.enums.AuthProvider;
import com.bintang.jwt.auth.repository.RoleRepository;
import com.bintang.jwt.auth.repository.UserRepository;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import jakarta.persistence.Access;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final AccessService accessService;
    private final RoleRepository roleRepository;

    private static final String DEFAULT_ROLE_NAME = "ROLE_USER";
    private final RefreshTokenService refreshTokenService;

    public AuthResult login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"LOCAL".equals(user.getProviderId())) {
            throw new RuntimeException("Please login using " + user.getAuthProvider());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return new AuthResult(accessToken, refreshToken.getToken());

    }

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authProvider("LOCAL")
                .providerId(null)
                .status(1L)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        Role defaultRole = roleRepository.findByNameAndIsDeletedFalse(DEFAULT_ROLE_NAME);
        if (defaultRole == null) {
            throw new RuntimeException("Default role USER not found");
        }
        accessService.assignRoleToUser(user.getId(), defaultRole.getId());
    }

}

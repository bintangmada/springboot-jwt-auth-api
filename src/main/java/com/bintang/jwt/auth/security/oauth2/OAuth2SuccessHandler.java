package com.bintang.jwt.auth.security.oauth2;

import com.bintang.jwt.auth.entity.Role;
import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.entity.enums.AuthProvider;
import com.bintang.jwt.auth.repository.RoleRepository;
import com.bintang.jwt.auth.repository.UserRepository;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import com.bintang.jwt.auth.service.AccessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final AccessService accessService;
    private final RoleRepository roleRepository;

    private static final String DEFAULT_ROLE_NAME = "ROLE_USER";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = authToken.getAuthorizedClientRegistrationId().toUpperCase();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        String providerId = oAuth2User.getName();

        User user = userRepository.findByEmail(email)
                .map(existing -> {
                    existing.setAuthProvider(provider);
                    existing.setProviderId(providerId);
                    return userRepository.save(existing);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(name)
                                .authProvider(provider)
                                .providerId(providerId)
                                .status(1L)
                                .isDeleted(false)
                                .build()
                ));

        Role defaultRole = roleRepository.findByNameAndIsDeletedFalse(DEFAULT_ROLE_NAME);
        if (defaultRole == null) {
            throw new RuntimeException("Default role USER not found");
        }
        accessService.assignRoleToUser(user.getId(), defaultRole.getId());

        String token = jwtUtil.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(), "", List.of()
                )
        );

        response.sendRedirect("http://frontend?token=" + token);
    }
}

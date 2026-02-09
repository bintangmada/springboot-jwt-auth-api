package com.bintang.jwt.auth.security.oauth2;

import com.bintang.jwt.auth.entity.User;
import com.bintang.jwt.auth.entity.enums.AuthProvider;
import com.bintang.jwt.auth.repository.UserRepository;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .name(oAuth2User.getAttribute("name"))
                                .authProvider(AuthProvider.GOOGLE)
                                .providerId(oAuth2User.getName())
                                .build()
                ));

        String token = jwtUtil.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(), "", List.of()
                )
        );

        response.sendRedirect("http://frontend?token=" + token);
    }
}

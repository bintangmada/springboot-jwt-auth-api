package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.auth.AuthResponse;
import com.bintang.jwt.auth.dto.auth.AuthResult;
import com.bintang.jwt.auth.dto.auth.LoginRequest;
import com.bintang.jwt.auth.dto.auth.RefreshTokenRequest;
import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.entity.RefreshToken;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import com.bintang.jwt.auth.security.user.CustomUserDetails;
import com.bintang.jwt.auth.service.AuthService;
import com.bintang.jwt.auth.service.RefreshTokenService;
import com.bintang.jwt.auth.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        AuthResult authResult = authService.login(request);

        // Save refresh token to cookie
        Cookie cookie = new Cookie("refreshToken", authResult.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthResponse(authResult.getAccessToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResult> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.findByToken(request.getRefreshToken());

        refreshTokenService.verifyExpiration(token);

        String accessToken = jwtUtil.generateToken(new CustomUserDetails(token.getUser()));

        AuthResult authResponse = new AuthResult(accessToken, request.getRefreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtil.extractRefreshTokenFromCookie(request);

        if (refreshToken != null) {
            refreshTokenService.delete(refreshToken);
        }

        cookieUtil.clearRefreshTokenCookie(response);

        return ResponseEntity.ok("Logout success");
    }

}

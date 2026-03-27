package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.auth.AuthResult;
import com.bintang.jwt.auth.dto.auth.LoginRequest;
import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import com.bintang.jwt.auth.service.AuthService;
import com.bintang.jwt.auth.service.RateLimitingService;
import com.bintang.jwt.auth.service.RefreshTokenService;
import com.bintang.jwt.auth.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import io.github.bucket4j.Bucket;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Menonaktifkan Spring Security Filter murni untuk fokus test Controller
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CookieUtil cookieUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private RateLimitingService rateLimitingService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Bucket mockBucket = Mockito.mock(Bucket.class);
        Mockito.when(mockBucket.tryConsume(1)).thenReturn(true);
        Mockito.when(rateLimitingService.resolveBucket(any())).thenReturn(mockBucket);
    }

    @Test
    void login_WithValidRequest_ShouldReturnAccessTokenAndCookie() throws Exception {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("valid@example.com");
        request.setPassword("password123");

        AuthResult dummyResult = new AuthResult("dummy-access-token", "dummy-refresh-token");
        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(dummyResult);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("dummy-access-token"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", "dummy-refresh-token"))
                .andExpect(cookie().httpOnly("refreshToken", true));
    }

    @Test
    void login_WithInvalidRequest_ShouldFailValidation() throws Exception {
        // Arrange: Email kurang atau format salah, password kosong
        LoginRequest request = new LoginRequest();
        request.setEmail("not-an-email");
        request.setPassword("");

        // Act & Assert: Harus gagal sebelum masuk ke AuthService karena @Valid
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_WithValidRequest_ShouldReturnCreated() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setName("Bintang");
        request.setEmail("bintang@example.com");
        request.setPassword("password123");

        // Tidak perlu stubbing untuk metode void, mockito akan mengabaikannya

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_WithTooManyRequests_ShouldReturn429() throws Exception {
        // Arrange
        Bucket mockBucket = Mockito.mock(Bucket.class);
        Mockito.when(mockBucket.tryConsume(1)).thenReturn(false); // Simulasi token bucket habis
        Mockito.when(rateLimitingService.resolveBucket(any())).thenReturn(mockBucket);

        LoginRequest request = new LoginRequest();
        request.setEmail("hacker@example.com");
        request.setPassword("password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewTokens() throws Exception {
        com.bintang.jwt.auth.dto.auth.RefreshTokenRequest request = new com.bintang.jwt.auth.dto.auth.RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        com.bintang.jwt.auth.entity.RefreshToken mockToken = new com.bintang.jwt.auth.entity.RefreshToken();
        mockToken.setToken("valid-refresh-token");
        com.bintang.jwt.auth.entity.User mockUser = new com.bintang.jwt.auth.entity.User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockToken.setUser(mockUser);

        Mockito.when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(mockToken);
        Mockito.when(jwtUtil.generateToken(any())).thenReturn("new-access-token");

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("valid-refresh-token"));
    }

    @Test
    void logout_ShouldClearCookies() throws Exception {
        Mockito.when(cookieUtil.extractRefreshTokenFromCookie(any())).thenReturn("valid-refresh-token");

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout success"));

        Mockito.verify(refreshTokenService).delete("valid-refresh-token");
        Mockito.verify(cookieUtil).clearRefreshTokenCookie(any());
    }
}

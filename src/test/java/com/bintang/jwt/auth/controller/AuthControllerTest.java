package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.auth.AuthResult;
import com.bintang.jwt.auth.dto.auth.LoginRequest;
import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import com.bintang.jwt.auth.service.AuthService;
import com.bintang.jwt.auth.service.RefreshTokenService;
import com.bintang.jwt.auth.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CookieUtil cookieUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
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

}

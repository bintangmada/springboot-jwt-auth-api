package com.bintang.jwt.auth;

import com.bintang.jwt.auth.controller.AuthController;
import com.bintang.jwt.auth.dto.auth.AuthResult;
import com.bintang.jwt.auth.dto.auth.LoginRequest;
import com.bintang.jwt.auth.security.jwt.JwtUtil;
import com.bintang.jwt.auth.service.AuthService;
import com.bintang.jwt.auth.service.RefreshTokenService;
import com.bintang.jwt.auth.util.CookieUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CookieUtil cookieUtil;

    @MockBean
    private com.bintang.jwt.auth.security.user.CustomUserDetailsService customUserDetailsService;

    // ==============================
    // LOGIN
    // ==============================
    @Test
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("user@mail.com", "password");

        AuthResult result = new AuthResult("access-token", "refresh-token");

        Mockito.when(authService.login(any())).thenReturn(result);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

}

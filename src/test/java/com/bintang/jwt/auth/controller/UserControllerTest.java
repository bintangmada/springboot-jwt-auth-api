package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.user.RegisterRequest;
import com.bintang.jwt.auth.dto.user.UpdateUserRequest;
import com.bintang.jwt.auth.dto.user.UserResponse;
import com.bintang.jwt.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.bintang.jwt.auth.config.WebMvcConfig;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean private com.bintang.jwt.auth.security.jwt.JwtUtil jwtUtil;
    @MockitoBean private com.bintang.jwt.auth.service.RefreshTokenService refreshTokenService;
    @MockitoBean private com.bintang.jwt.auth.util.CookieUtil cookieUtil;
    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockitoBean private com.bintang.jwt.auth.service.RateLimitingService rateLimitingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Bintang");
        request.setEmail("bintang@example.com");
        request.setPassword("password123");

        UserResponse response = UserResponse.builder()
                .id(1L)
                .name("Bintang")
                .email("bintang@example.com")
                .build();

        Mockito.when(userService.create(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bintang"));
    }

    @Test
    void getById_ShouldReturn200() throws Exception {
        UserResponse response = UserResponse.builder().id(1L).name("Bintang").build();
        Mockito.when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bintang"));
    }

    @Test
    void getAll_ShouldReturn200() throws Exception {
        UserResponse response = UserResponse.builder().id(1L).name("Bintang").build();
        Mockito.when(userService.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bintang"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");

        UserResponse response = UserResponse.builder().id(1L).name("Updated Name").build();
        Mockito.when(userService.update(eq(1L), any(UpdateUserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(1L);
    }
}

package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.permission.PermissionRequest;
import com.bintang.jwt.auth.dto.permission.PermissionResponse;
import com.bintang.jwt.auth.service.PermissionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PermissionController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

    @MockitoBean private com.bintang.jwt.auth.security.jwt.JwtUtil jwtUtil;
    @MockitoBean private com.bintang.jwt.auth.service.RefreshTokenService refreshTokenService;
    @MockitoBean private com.bintang.jwt.auth.util.CookieUtil cookieUtil;
    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockitoBean private com.bintang.jwt.auth.service.RateLimitingService rateLimitingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturn201() throws Exception {
        PermissionRequest request = new PermissionRequest();
        request.setName("READ_DATA");

        PermissionResponse response = PermissionResponse.builder().id(1L).name("READ_DATA").build();
        Mockito.when(permissionService.create(any(PermissionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("READ_DATA"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        PermissionRequest request = new PermissionRequest();
        request.setName("WRITE_DATA");

        PermissionResponse response = PermissionResponse.builder().id(1L).name("WRITE_DATA").build();
        Mockito.when(permissionService.update(eq(1L), any(PermissionRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/permissions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("WRITE_DATA"));
    }

    @Test
    void delete_ShouldReturn200() throws Exception {
        // The controller currently returns 200 OK void instead of 204 No Content
        mockMvc.perform(delete("/api/permissions/1"))
                .andExpect(status().isOk());

        Mockito.verify(permissionService).delete(1L);
    }
}

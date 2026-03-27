package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.dto.role.RoleRequest;
import com.bintang.jwt.auth.dto.role.RoleResponse;
import com.bintang.jwt.auth.service.RoleService;
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
    controllers = RoleController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean private com.bintang.jwt.auth.security.jwt.JwtUtil jwtUtil;
    @MockitoBean private com.bintang.jwt.auth.service.RefreshTokenService refreshTokenService;
    @MockitoBean private com.bintang.jwt.auth.util.CookieUtil cookieUtil;
    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockitoBean private com.bintang.jwt.auth.service.RateLimitingService rateLimitingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_ShouldReturn201() throws Exception {
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");

        RoleResponse response = RoleResponse.builder().id(1L).name("ADMIN").build();
        Mockito.when(roleService.create(any(RoleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void getAll_ShouldReturn200() throws Exception {
        RoleResponse response = RoleResponse.builder().id(1L).name("ADMIN").build();
        Mockito.when(roleService.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"));
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        RoleRequest request = new RoleRequest();
        request.setName("SUPER_ADMIN");

        RoleResponse response = RoleResponse.builder().id(1L).name("SUPER_ADMIN").build();
        Mockito.when(roleService.update(eq(1L), any(RoleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPER_ADMIN"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(roleService).delete(1L);
    }
}

package com.bintang.jwt.auth.controller;

import com.bintang.jwt.auth.service.AccessService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import com.bintang.jwt.auth.config.WebMvcConfig;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AccessController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcConfig.class)
)
@AutoConfigureMockMvc(addFilters = false)
class AccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccessService accessService;

    @MockitoBean private com.bintang.jwt.auth.security.jwt.JwtUtil jwtUtil;
    @MockitoBean private com.bintang.jwt.auth.service.RefreshTokenService refreshTokenService;
    @MockitoBean private com.bintang.jwt.auth.util.CookieUtil cookieUtil;
    @MockitoBean private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockitoBean private com.bintang.jwt.auth.service.RateLimitingService rateLimitingService;

    @Test
    void assignRoleToUser_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/access/user-role")
                .param("userId", "1")
                .param("roleId", "2"))
                .andExpect(status().isOk());

        Mockito.verify(accessService).assignRoleToUser(1L, 2L);
    }

    @Test
    void revokeRoleFromUser_ShouldReturn404() throws Exception {
        // Warning: Currently the controller returns 404 NOT FOUND artificially
        mockMvc.perform(delete("/api/access/user-role")
                .param("userId", "1")
                .param("roleId", "2"))
                .andExpect(status().isNotFound());

        Mockito.verify(accessService).revokeRoleFromUser(1L, 2L);
    }

    @Test
    void assignPermissionToRole_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/access/role-permission")
                .param("roleId", "1")
                .param("permissionId", "3"))
                .andExpect(status().isOk());

        Mockito.verify(accessService).assignPermissionToRole(1L, 3L);
    }

    @Test
    void revokePermissionFromRole_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/access/role-permission")
                .param("roleId", "1")
                .param("permissionId", "3"))
                .andExpect(status().isNotFound());

        Mockito.verify(accessService).revokePermissionFromRole(1L, 3L);
    }

    @Test
    void assignPermissionToUser_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/access/user-permission")
                .param("userId", "1")
                .param("permissionId", "4"))
                .andExpect(status().isOk());

        Mockito.verify(accessService).assignPermissionToUser(1L, 4L);
    }

    @Test
    void revokePermissionFromUser_ShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/access/user-permission")
                .param("userId", "1")
                .param("permissionId", "4"))
                .andExpect(status().isNotFound());

        Mockito.verify(accessService).revokePermissionFromUser(1L, 4L);
    }
}

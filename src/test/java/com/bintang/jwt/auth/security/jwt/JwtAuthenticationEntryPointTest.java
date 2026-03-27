package com.bintang.jwt.auth.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAuthenticationEntryPointTest {

    @Test
    void commence_ShouldReturn401AndJson() throws IOException, ServletException {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected");
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        AuthenticationException exception = new org.springframework.security.authentication.BadCredentialsException("Bad creds");

        entryPoint.commence(request, response, exception);

        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());

        String jsonPayload = response.getContentAsString();
        assertTrue(jsonPayload.contains("\"status\": 401"));
        assertTrue(jsonPayload.contains("\"error\": \"UNAUTHORIZED\""));
        assertTrue(jsonPayload.contains("\"path\": \"/api/protected\""));
    }
}

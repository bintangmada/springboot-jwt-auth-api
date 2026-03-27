package com.bintang.jwt.auth.exception;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomAccessDeniedHandlerTest {

    @Test
    void handle_ShouldReturn403AndJson() throws IOException {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/secured");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException exception = new AccessDeniedException("Denied");

        handler.handle(request, response, exception);

        assertEquals(403, response.getStatus());
        assertEquals("application/json", response.getContentType());
        
        String jsonPayload = response.getContentAsString();
        assertTrue(jsonPayload.contains("\"status\": 403"));
        assertTrue(jsonPayload.contains("\"error\": \"FORBIDDEN\""));
        assertTrue(jsonPayload.contains("\"path\": \"/api/secured\""));
    }
}

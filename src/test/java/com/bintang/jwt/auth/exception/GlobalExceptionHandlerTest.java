package com.bintang.jwt.auth.exception;

import com.bintang.jwt.auth.dto.exception.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
    }

    @Test
    void handleNotFound_ShouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleBadRequest_ShouldReturn400() {
        BadRequestException ex = new BadRequestException("Bad request");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bad request", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorized_ShouldReturn401() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorized(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    @Test
    void handleTooManyRequests_ShouldReturn429() {
        TooManyRequestsException ex = new TooManyRequestsException("Limit exceeded");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTooManyRequests(ex, request);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Limit exceeded", response.getBody().getMessage());
    }

    @Test
    void handleAll_ShouldReturn500() {
        Exception ex = new Exception("Internal error");
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAll(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().getMessage());
    }

    @Test
    void handleValidationException_ShouldReturn400WithErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("object", "email", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "password", "too short");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("email: must not be blank, password: too short", response.getBody().getMessage());
    }

    @Test
    void handleValidationException_EmptyErrors_ShouldReturnDefaultMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
    }
}

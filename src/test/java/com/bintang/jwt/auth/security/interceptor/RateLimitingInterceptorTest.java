package com.bintang.jwt.auth.security.interceptor;

import com.bintang.jwt.auth.exception.TooManyRequestsException;
import com.bintang.jwt.auth.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class RateLimitingInterceptorTest {

    @Mock
    private RateLimitingService rateLimitingService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Bucket mockBucket;

    @InjectMocks
    private RateLimitingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        Mockito.when(rateLimitingService.resolveBucket(anyString())).thenReturn(mockBucket);
    }

    @Test
    void preHandle_WithTokensAvailable_ShouldPassAndReturnTrue() {
        // Arrange
        Mockito.when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.5");
        Mockito.when(mockBucket.tryConsume(1)).thenReturn(true);

        // Act
        boolean result = interceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        Mockito.verify(rateLimitingService).resolveBucket("10.0.0.5");
    }

    @Test
    void preHandle_WithNoTokens_ShouldThrowTooManyRequestsException() {
        // Arrange
        Mockito.when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        Mockito.when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        Mockito.when(mockBucket.tryConsume(1)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(TooManyRequestsException.class, () -> 
            interceptor.preHandle(request, response, new Object())
        );
        
        assertTrue(exception.getMessage().contains("Terlalu banyak percobaan"));
        Mockito.verify(rateLimitingService).resolveBucket("192.168.1.1");
    }
}

package com.bintang.jwt.auth.service;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitingServiceTest {

    private RateLimitingService rateLimitingService;

    @BeforeEach
    void setUp() {
        rateLimitingService = new RateLimitingService();
    }

    @Test
    void resolveBucket_WithNewIp_ShouldReturnNewBucketWith5Tokens() {
        // Arrange
        String ipAddress = "192.168.1.1";

        // Act
        Bucket bucket = rateLimitingService.resolveBucket(ipAddress);

        // Assert
        assertNotNull(bucket);
        assertTrue(bucket.tryConsume(1)); // Konsumsi 1 token berhasil
        assertTrue(bucket.tryConsume(4)); // Konsumsi 4 token tersisa berhasil
        assertFalse(bucket.tryConsume(1)); // Konsumsi ke-6 gagal (maksimal 5)
    }

    @Test
    void resolveBucket_WithSameIp_ShouldReturnSameBucketInstance() {
        // Arrange
        String ipAddress = "10.0.0.1";

        // Act
        Bucket bucket1 = rateLimitingService.resolveBucket(ipAddress);
        Bucket bucket2 = rateLimitingService.resolveBucket(ipAddress);

        // Assert
        assertSame(bucket1, bucket2, "Harus mereturn instance bucket yang persis sama dari cache");
    }

    @Test
    void resolveBucket_WithDifferentIp_ShouldReturnIndependentBuckets() {
        // Arrange
        String ip1 = "192.168.1.100";
        String ip2 = "192.168.1.200";

        // Act
        Bucket bucket1 = rateLimitingService.resolveBucket(ip1);
        Bucket bucket2 = rateLimitingService.resolveBucket(ip2);

        // Assert
        assertNotSame(bucket1, bucket2, "IP berbeda harus mendapat bucket yang berbeda");
        
        // Kosongkan bucket 1
        assertTrue(bucket1.tryConsume(5));
        assertFalse(bucket1.tryConsume(1));
        
        // Bucket 2 harus tetap penuh (5 token)
        assertTrue(bucket2.tryConsume(5));
    }
}

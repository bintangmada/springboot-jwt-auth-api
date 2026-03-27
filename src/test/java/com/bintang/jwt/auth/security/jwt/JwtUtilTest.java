package com.bintang.jwt.auth.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private UserDetails userDetails;

    // Kunci Rahasia untuk keperluan testing (harus cukup panjang untuk algoritma HS256 minimal 256 bits)
    private final String secret = "12345678901234567890123456789012345678901234567890";
    private final long expiration = 1000 * 60 * 60; // 1 jam

    @BeforeEach
    void setUp() {
        // Inisialisasi object sebelum setiap metode @Test berjalan
        jwtUtil = new JwtUtil(secret, expiration);
        jwtUtil.checkJwtSecretKey(); // Menginisialisasi 'signingKey' yang ada di PostConstruct class aslinya
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        // Arrange: Atur tingkah laku (mock) untuk objek UserDetails
        when(userDetails.getUsername()).thenReturn("bintang@example.com");

        // Act: Panggil metode yang akan diuji
        String token = jwtUtil.generateToken(userDetails);

        // Assert: Pastikan outputnya sesuai dengan ekspektasi
        assertNotNull(token);
        assertFalse(token.trim().isEmpty());
        // JWT asli selau terdiri dari 3 bagian yang dipisahkan oleh titik (.)
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        when(userDetails.getUsername()).thenReturn("bintang@example.com");
        String token = jwtUtil.generateToken(userDetails); // Bikin token asli dulu

        // Ekstrak username dari token yang dibuat
        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals("bintang@example.com", extractedUsername);
    }

    @Test
    void extractUsername_WithInvalidSignature_ShouldThrowSignatureException() {
        when(userDetails.getUsername()).thenReturn("bintang@example.com");
        String token = jwtUtil.generateToken(userDetails);

        // Ubah struktur string token secara sengaja agar tandatangannya tidak valid
        String tamperedToken = token + "xyz";

        // Memastikan sistem melempar SignatureException (keamanan bekerja)
        assertThrows(SignatureException.class, () -> jwtUtil.extractUsername(tamperedToken));
    }

    @Test
    void extractUsername_WithMalformedToken_ShouldThrowMalformedJwtException() {
        // Uji dengan string yang sama sekali bukan JWT
        String malformedToken = "ini.bukan.token.jwt";

        assertThrows(MalformedJwtException.class, () -> jwtUtil.extractUsername(malformedToken));
    }

    @Test
    void extractUsername_WithExpiredToken_ShouldThrowExpiredJwtException() throws InterruptedException {
        // Setup JwtUtil khusus dengan waktu kadaluarsa super cepat: 1 Milidetik
        JwtUtil fastExpiringJwtUtil = new JwtUtil(secret, 1L);
        fastExpiringJwtUtil.checkJwtSecretKey();
        
        when(userDetails.getUsername()).thenReturn("bintang@example.com");
        String token = fastExpiringJwtUtil.generateToken(userDetails);

        // Tidurkan thread sebentar (10 milidetik) untuk memastikan token benar-benar sudah 'expired'
        Thread.sleep(10);

        assertThrows(ExpiredJwtException.class, () -> fastExpiringJwtUtil.extractUsername(token));
    }
}

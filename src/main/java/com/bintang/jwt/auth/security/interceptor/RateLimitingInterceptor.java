package com.bintang.jwt.auth.security.interceptor;

import com.bintang.jwt.auth.exception.TooManyRequestsException;
import com.bintang.jwt.auth.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        // Membaca IP asli jika berada di balik proxy (seperti Nginx atau Cloudflare)
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }

        Bucket tokenBucket = rateLimitingService.resolveBucket(ipAddress);

        // Consume 1 token untuk setiap request. Jika return false, berarti bucket kosong.
        if (tokenBucket.tryConsume(1)) {
            return true;
        }

        log.warn("Brute force detected / Rate limit exceeded for IP: {}", ipAddress);
        throw new TooManyRequestsException("Terlalu banyak percobaan Login/Register. Silakan coba lagi dalam beberapa menit.");
    }
}

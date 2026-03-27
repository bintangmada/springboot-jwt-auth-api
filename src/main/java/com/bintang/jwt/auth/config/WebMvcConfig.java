package com.bintang.jwt.auth.config;

import com.bintang.jwt.auth.security.interceptor.RateLimitingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitingInterceptor rateLimitingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Melindungi jalur-jalur autentikasi publik yang krusial dari serangan brute force
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/auth/login", "/auth/register");
    }
}

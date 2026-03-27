package com.bintang.jwt.auth.util;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class CookieUtilTest {

    private CookieUtil cookieUtil;

    @BeforeEach
    void setUp() {
        cookieUtil = new CookieUtil();
    }

    @Test
    void extractRefreshTokenFromCookie_ShouldReturnToken_WhenCookieExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("refreshToken", "dummy-refresh-token");
        request.setCookies(cookie);

        String token = cookieUtil.extractRefreshTokenFromCookie(request);

        assertEquals("dummy-refresh-token", token);
    }

    @Test
    void extractRefreshTokenFromCookie_ShouldReturnNull_WhenNoCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String token = cookieUtil.extractRefreshTokenFromCookie(request);

        assertNull(token);
    }

    @Test
    void extractRefreshTokenFromCookie_ShouldReturnNull_WhenRefreshTokenCookieNotPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("otherCookie", "some-value");
        request.setCookies(cookie);

        String token = cookieUtil.extractRefreshTokenFromCookie(request);

        assertNull(token);
    }

    @Test
    void clearRefreshTokenCookie_ShouldAddExpiredCookieToResponse() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieUtil.clearRefreshTokenCookie(response);

        Cookie cookie = response.getCookie("refreshToken");
        assertNotNull(cookie);
        assertNull(cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.getSecure());
        assertEquals("/", cookie.getPath());
    }
}

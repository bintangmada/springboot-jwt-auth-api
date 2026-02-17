package com.bintang.jwt.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    public String extractRefreshTokenFromCookie(HttpServletRequest request){
        if(request.getCookies() == null){
            return null;
        }

        for(Cookie cookie : request.getCookies()){
            if(REFRESH_TOKEN_COOKIE.equals(cookie.getName())){
                return cookie.getValue();
            }
        }

        return null;
    }

    public void clearRefreshTokenCookie(HttpServletResponse response){
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

}

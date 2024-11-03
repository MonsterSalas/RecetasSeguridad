package com.recetas.recetas.service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CookieService {
    
    private static final Logger logger = LoggerFactory.getLogger(CookieService.class);
    private static final String JWT_COOKIE_NAME = "JWT-TOKEN";
    private static final int COOKIE_MAX_AGE = 7200; // 2 horas

    public void createJwtCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, token);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setSecure(false); // Cambiar a true en producción
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.debug("Cookie JWT creada: {}", token);
    }

    public void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setSecure(false); // Cambiar a true en producción
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        logger.debug("Cookie JWT eliminada");
    }
}
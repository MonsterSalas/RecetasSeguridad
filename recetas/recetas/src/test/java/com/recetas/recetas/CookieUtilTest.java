package com.recetas.recetas;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.recetas.recetas.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CookieUtilTest {

    @Test
    void getJwtFromCookies_ConCookieJWT_DebeRetornarToken() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Cookie[] cookies = {
            new Cookie("other-cookie", "other-value"),
            new Cookie("JWT-TOKEN", "test-token")
        };
        when(request.getCookies()).thenReturn(cookies);

        String result = CookieUtil.getJwtFromCookies(request);
        assertEquals("test-token", result);
    }

    @Test
    void getJwtFromCookies_SinCookieJWT_DebeRetornarNull() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Cookie[] cookies = {
            new Cookie("other-cookie", "other-value")
        };
        when(request.getCookies()).thenReturn(cookies);

        String result = CookieUtil.getJwtFromCookies(request);
        assertNull(result);
    }

    @Test
    void getJwtFromCookies_SinCookies_DebeRetornarNull() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        String result = CookieUtil.getJwtFromCookies(request);
        assertNull(result);
    }

    @Test
    void getJwtFromCookies_RequestNull_DebeRetornarNull() {
        String result = CookieUtil.getJwtFromCookies(null);
        assertNull(result);
    }
}
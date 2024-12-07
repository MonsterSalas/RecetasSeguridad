package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.recetas.recetas.service.CookieService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CookieServiceTest {

    private CookieService cookieService;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cookieService = new CookieService();
    }

    @Test
    void createJwtCookie_DebeCreayCookieConAtributosCorrectos() {
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        
        cookieService.createJwtCookie("test-token", response);
        
        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        
        assertEquals("JWT-TOKEN", cookie.getName());
        assertEquals("test-token", cookie.getValue());
        assertEquals(7200, cookie.getMaxAge());
        assertFalse(cookie.getSecure());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }

    @Test
    void clearJwtCookie_DebeLimpiarCookieConAtributosCorrectos() {
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        
        cookieService.clearJwtCookie(response);
        
        verify(response).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        
        assertEquals("JWT-TOKEN", cookie.getName());
        assertNull(cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertFalse(cookie.getSecure());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }
}
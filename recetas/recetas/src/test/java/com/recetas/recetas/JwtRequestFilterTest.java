package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.recetas.recetas.security.JwtRequestFilter;
import com.recetas.recetas.security.JwtUtil;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtRequestFilterTest {

    private static class TestJwtRequestFilter extends JwtRequestFilter {
        @Override
        public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException, java.io.IOException {
            super.doFilterInternal(request, response, chain);
        }
    }

    @InjectMocks
    private TestJwtRequestFilter jwtRequestFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_ConTokenValido_DebeAutenticar() throws Exception {
        Cookie jwtCookie = new Cookie("JWT-TOKEN", "valid-token");
        request.setCookies(jwtCookie);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtUtil.validateToken("valid-token", userDetails)).thenReturn(true);

        jwtRequestFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_SinToken_NoDebeAutenticar() throws Exception {
        jwtRequestFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_ConTokenInvalido_NoDebeAutenticar() throws Exception {
        Cookie jwtCookie = new Cookie("JWT-TOKEN", "invalid-token");
        request.setCookies(jwtCookie);
        when(jwtUtil.extractUsername("invalid-token")).thenReturn(null);

        jwtRequestFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilter_ConTokenExpirado_NoDebeAutenticar() throws Exception {
        Cookie jwtCookie = new Cookie("JWT-TOKEN", "expired-token");
        request.setCookies(jwtCookie);

        UserDetails userDetails = mock(UserDetails.class);
        when(jwtUtil.extractUsername("expired-token")).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(jwtUtil.validateToken("expired-token", userDetails)).thenReturn(false);

        jwtRequestFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
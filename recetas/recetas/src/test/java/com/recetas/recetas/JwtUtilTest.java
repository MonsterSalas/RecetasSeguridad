package com.recetas.recetas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.recetas.recetas.security.JwtUtil;

import java.util.Date;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    
    private static class TestJwtUtil extends JwtUtil {
        @Override
        protected void init() {
            super.init();
        }
    }
    
    private static final String TEST_SECRET = 
        "testsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkey" +
        "testsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkeytestsecretkey";
    
    private TestJwtUtil jwtUtil;
    
    @Mock
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new TestJwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L);
        when(userDetails.getUsername()).thenReturn("testUser");
        jwtUtil.init();
    }

    @Test
    void generateToken_DebeCrearTokenValido() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertEquals("testUser", jwtUtil.extractUsername(token));
    }

    @Test
    void validateToken_TokenValido_DebeRetornarTrue() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_TokenExpirado_DebeRetornarFalse() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -3600000L);
        String token = jwtUtil.generateToken(userDetails);
        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void extractUsername_TokenInvalido_DebeRetornarNull() {
        assertNull(jwtUtil.extractUsername("invalid.token.here"));
    }

    @Test
    void validateToken_TokenInvalido_DebeRetornarFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token", userDetails));
    }
    
    @Test
    void validateToken_UsuariosDiferentes_DebeRetornarFalse() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otroUsuario = mock(UserDetails.class);
        when(otroUsuario.getUsername()).thenReturn("otroUsuario");
        assertFalse(jwtUtil.validateToken(token, otroUsuario));
    }
}
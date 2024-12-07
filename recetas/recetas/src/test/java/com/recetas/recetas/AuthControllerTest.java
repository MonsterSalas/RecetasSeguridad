package com.recetas.recetas;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import com.recetas.recetas.controllers.AuthController;
import com.recetas.recetas.security.JwtUtil;
import com.recetas.recetas.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.security.core.context.SecurityContext;
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CookieService cookieService;
    @InjectMocks
    private AuthController authController;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Model model;

    @Test
    void showLoginForm_WithValidToken_RedirectsToHome() {
        // Setup
        Cookie jwtCookie = new Cookie("JWT-TOKEN", "valid-token");
        Cookie[] cookies = {jwtCookie};
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        
        when(request.getCookies()).thenReturn(cookies);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("testUser");

        String result = authController.showLoginForm(request);
        
        assertEquals("redirect:/home", result);
    }

    @Test
    void showLoginForm_WithoutCookies_ShowsLoginPage() {
        when(request.getCookies()).thenReturn(null);
        String result = authController.showLoginForm(request);
        assertEquals("login", result);
    }

    @Test
    void login_Successful() {
        String username = "testUser";
        String password = "password";
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        String result = authController.login(username, password, response, model);

        assertEquals("redirect:/home", result);
        verify(cookieService).createJwtCookie("jwt-token", response);
    }

    @Test
    void login_Failed() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        String result = authController.login("testUser", "wrongPassword", response, model);

        assertEquals("login", result);
        verify(model).addAttribute("error", "Usuario o contraseña incorrectos");
    }

    @Test
    void logout_Successful() {
        String result = authController.logout(response);
        assertEquals("redirect:/login?logout", result);
        verify(cookieService).clearJwtCookie(response);
    }
}
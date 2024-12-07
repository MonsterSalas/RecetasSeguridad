package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.recetas.recetas.controllers.HomeController;
import com.recetas.recetas.service.RecetaService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class HomeControllerTest {

    @Mock
    private RecetaService recetaService;
    @Mock
    private Model model;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testIndex() {
        List<String> expectedBanners = Arrays.asList(
            "¡Nueva línea de utensilios de cocina!",
            "Descuentos en productos gourmet",
            "Clases de cocina disponibles"
        );

        String viewName = homeController.index(model);

        verify(recetaService).obtenerRecetasRecientes();
        verify(recetaService).obtenerRecetasPopulares();
        verify(model).addAttribute("banners", expectedBanners);
        assertEquals("index", viewName);
    }

    @Test
    void testHomeWithJwtCookie() {
        Cookie jwtCookie = new Cookie("JWT-TOKEN", "test-jwt-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(authentication.getName()).thenReturn("testUser");

        String viewName = homeController.home(request, model);

        verify(model).addAttribute("username", "testUser");
        verify(model).addAttribute("jwt", "test-jwt-token");
        verify(recetaService).obtenerTodasRecetas();
        assertEquals("home", viewName);
    }

    @Test
    void testHomeWithoutCookies() {
        when(request.getCookies()).thenReturn(null);
        when(authentication.getName()).thenReturn("testUser");

        String viewName = homeController.home(request, model);

        verify(model).addAttribute("jwt", "No se encontró token JWT");
        assertEquals("home", viewName);
    }

    @Test
    void testHomeWithoutJwtCookie() {
        Cookie otherCookie = new Cookie("OTHER", "value");
        when(request.getCookies()).thenReturn(new Cookie[]{otherCookie});
        when(authentication.getName()).thenReturn("testUser");

        String viewName = homeController.home(request, model);

        verify(model).addAttribute("jwt", "No se encontró token JWT");
        assertEquals("home", viewName);
    }
}
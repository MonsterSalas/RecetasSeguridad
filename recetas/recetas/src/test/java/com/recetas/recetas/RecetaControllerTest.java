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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import static org.mockito.ArgumentMatchers.eq;

import com.recetas.recetas.controllers.AuthController;
import com.recetas.recetas.controllers.RecetaController;
import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.User;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UserRepository;
import com.recetas.recetas.security.JwtUtil;
import com.recetas.recetas.service.ContentFilterService;
import com.recetas.recetas.service.CookieService;
import com.recetas.recetas.service.RecetaService;
import com.recetas.recetas.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.springframework.security.core.context.SecurityContext;
@ExtendWith(MockitoExtension.class)
class RecetaControllerTest {
    @Mock
    
    private RecetaService recetaService;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecetaRepository recetaRepository;
    @Mock
    private ComentarioRepository comentarioRepository;
    @Mock
    private ContentFilterService contentFilterService;
    @Mock
    private Model model;
    @Mock
    private Authentication authentication;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private RecetaController recetaController;

    @Test
    void mostrarFormularioCrear_DebeRetornarVistaCorrecta() {
        String vista = recetaController.mostrarFormularioCrear(model);
        assertEquals("crear-receta", vista);
        verify(model).addAttribute(eq("receta"), any(Receta.class));
    }

    @Test
    void procesarCrearReceta_Exitoso() throws Exception {
        User usuario = new User();
        Receta receta = new Receta();
        receta.setId(1L);
        UserDetails userDetails = mock(UserDetails.class);
        List<MultipartFile> archivos = new ArrayList<>();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("usuario");
        when(userService.findByUsername("usuario")).thenReturn(usuario);
        when(recetaService.crearReceta(any(Receta.class), anyList())).thenReturn(receta);

        String resultado = recetaController.procesarCrearReceta(receta, archivos, authentication);

        assertEquals("redirect:/receta/1", resultado);
    }

    @Test
    void verReceta_Exitoso() throws Exception {
        Long id = 1L;
        Receta receta = new Receta();
        when(recetaService.obtenerReceta(id)).thenReturn(receta);

        String resultado = recetaController.verReceta(id, model);

        assertEquals("detalle-receta", resultado);
        verify(model).addAttribute("receta", receta);
    }

    @Test
    void misRecetas_Exitoso() {
        User usuario = new User();
        List<Receta> recetas = new ArrayList<>();
        
        when(authentication.getName()).thenReturn("usuario");
        when(userService.findByUsername("usuario")).thenReturn(usuario);
        when(recetaService.obtenerRecetasPorUsuario(usuario)).thenReturn(recetas);

        String resultado = recetaController.misRecetas(model, authentication);

        assertEquals("mis-recetas", resultado);
        verify(model).addAttribute("misRecetas", recetas);
    }

    @Test
    void comentarReceta_ContenidoOfensivo() {
        when(contentFilterService.containsOffensiveContent(anyString())).thenReturn(true);

        ResponseEntity<?> response = recetaController.comentarReceta(1L, "contenido ofensivo", authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
    }

    @Test
    void valorarReceta_Exitoso() {
        Long id = 1L;
        User usuario = new User();
        Receta receta = new Receta();
        receta.setValoraciones(new ArrayList<>());
        UserDetails userDetails = mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("usuario");
        when(userService.findByUsername("usuario")).thenReturn(usuario);
        when(recetaService.obtenerReceta(id)).thenReturn(receta);

        ResponseEntity<String> response = recetaController.valorarReceta(id, 5, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Valoración guardada exitosamente", response.getBody());
    }
    @Test
    void procesarCrearReceta_ErrorUsuarioNoEncontrado() {
        Receta receta = new Receta();
        List<MultipartFile> archivos = new ArrayList<>();
        UserDetails userDetails = mock(UserDetails.class);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("usuario");
        when(userService.findByUsername("usuario")).thenReturn(null);
        
        String resultado = recetaController.procesarCrearReceta(receta, archivos, authentication);
        
        assertEquals("redirect:/receta/crear?error=true", resultado);
    }

    @Test
    void eliminarReceta_Exitoso() {
        Long id = 1L;
        String resultado = recetaController.eliminarReceta(id, authentication, redirectAttributes);
        
        assertEquals("redirect:/receta/mis-recetas", resultado);
        verify(redirectAttributes).addFlashAttribute("mensaje", "Receta eliminada exitosamente");
        verify(recetaService).eliminarReceta(id, authentication);
    }

    @Test
    void eliminarReceta_SinPermiso() {
        Long id = 1L;
        doThrow(new SecurityException()).when(recetaService).eliminarReceta(id, authentication);
        
        String resultado = recetaController.eliminarReceta(id, authentication, redirectAttributes);
        
        assertEquals("redirect:/receta/mis-recetas", resultado);
        verify(redirectAttributes).addFlashAttribute("error", "No tienes permiso para eliminar esta receta");
    }

    @Test
    void comentarReceta_RecetaNoEncontrada() {
        when(authentication.getName()).thenReturn("usuario");
        when(userRepository.findByUsername("usuario")).thenReturn(Optional.of(new User()));
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());
        
        ResponseEntity<?> response = recetaController.comentarReceta(1L, "contenido", authentication);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Receta no encontrada", response.getBody());
    }

    @Test
    void comentarReceta_UsuarioNoEncontrado() {
        when(authentication.getName()).thenReturn("usuario");
        when(userRepository.findByUsername("usuario")).thenReturn(Optional.empty());
        
        ResponseEntity<?> response = recetaController.comentarReceta(1L, "contenido", authentication);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    void valorarReceta_Error() {
        Long id = 1L;
        UserDetails userDetails = mock(UserDetails.class);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("usuario");
        when(userService.findByUsername("usuario")).thenReturn(new User());
        when(recetaService.obtenerReceta(id)).thenThrow(new RuntimeException());
        
        ResponseEntity<String> response = recetaController.valorarReceta(id, 5, authentication);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al guardar valoración", response.getBody());
    }
}
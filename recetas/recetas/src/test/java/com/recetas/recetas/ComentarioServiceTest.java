package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.recetas.recetas.entity.Comentario;
import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.User;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UserRepository;
import com.recetas.recetas.service.ComentarioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;

class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;
    
    @Mock
    private RecetaRepository recetaRepository;
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    private User testUser;
    private Receta testReceta;
    private Comentario testComentario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        
        testReceta = new Receta();
        testReceta.setId(1L);
        
        testComentario = new Comentario();
        testComentario.setContenido("Test comentario");
    }

    @Test
    void guardarComentario_CuandoExistenUsuarioYReceta_DebeGuardarComentario() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(testReceta));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(testComentario);

        comentarioService.guardarComentario(1L, "Test comentario", "testUser");

        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void guardarComentario_CuandoNoExisteUsuario_NoDebeGuardarComentario() {
        when(userRepository.findByUsername("noExiste")).thenReturn(Optional.empty());

        comentarioService.guardarComentario(1L, "Test comentario", "noExiste");

        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    @Test
    void guardarComentario_CuandoNoExisteReceta_NoDebeGuardarComentario() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(recetaRepository.findById(999L)).thenReturn(Optional.empty());

        comentarioService.guardarComentario(999L, "Test comentario", "testUser");

        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    @Test
    void guardarComentario_DebeAsignarFechaCreacion() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(testReceta));

        comentarioService.guardarComentario(1L, "Test comentario", "testUser");

        verify(comentarioRepository).save(argThat(comentario -> 
            comentario.getFechaCreacion() != null &&
            comentario.getFechaCreacion().isBefore(LocalDateTime.now().plusSeconds(1))
        ));
    }
}
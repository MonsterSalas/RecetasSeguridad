package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.recetas.recetas.entity.Comentario;
import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.RecetaMedia;
import com.recetas.recetas.entity.User;
import com.recetas.recetas.entity.Valoracion;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaMediaRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.ValoracionRepository;
import com.recetas.recetas.service.RecetaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RecetaServiceTest {

    @Mock
    private RecetaRepository recetaRepository;
    
    @Mock
    private RecetaMediaRepository mediaRepository;
    
    @Mock
    private ComentarioRepository comentarioRepository;
    
    @Mock
    private ValoracionRepository valoracionRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RecetaService recetaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(recetaService, "uploadPath", "src/test/resources/uploads");
    }


    @Test
    void obtenerRecetasRecientes_DeberiaRetornarTop10() {
        List<Receta> recetas = Arrays.asList(new Receta(), new Receta());
        when(recetaRepository.findTop10ByOrderByFechaCreacionDesc()).thenReturn(recetas);

        List<Receta> resultado = recetaService.obtenerRecetasRecientes();

        assertEquals(2, resultado.size());
        verify(recetaRepository).findTop10ByOrderByFechaCreacionDesc();
    }

    @Test
    void crearReceta_ConArchivosMultimedia_DeberiaCrearRecetaYMedia() throws IOException {
        Receta receta = new Receta();
        receta.setTitulo("Test Receta");

        MockMultipartFile file = new MockMultipartFile(
            "test.jpg", "test.jpg", "image/jpeg", "test".getBytes()
        );

        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta resultado = recetaService.crearReceta(receta, List.of(file));

        assertNotNull(resultado);
        verify(mediaRepository).save(any(RecetaMedia.class));
    }

    @Test
    void crearReceta_SinTitulo_DeberiaLanzarExcepcion() {
        Receta receta = new Receta();
        
        assertThrows(IllegalArgumentException.class, () -> 
            recetaService.crearReceta(receta, new ArrayList<>())
        );
    }

    @Test
    void obtenerReceta_ExisteId_DeberiaRetornarReceta() {
        Receta receta = new Receta();
        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        Receta resultado = recetaService.obtenerReceta(1L);

        assertNotNull(resultado);
        verify(recetaRepository).findById(1L);
    }

    @Test
    void obtenerReceta_NoExisteId_DeberiaLanzarExcepcion() {
        when(recetaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            recetaService.obtenerReceta(1L)
        );
    }

    @Test
    void agregarComentario_DeberiaGuardarComentario() {
        Receta receta = new Receta();
        receta.setId(1L);
        Comentario comentario = new Comentario();

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        recetaService.agregarComentario(1L, comentario);

        verify(comentarioRepository).save(any(Comentario.class));
        assertNotNull(comentario.getFechaCreacion());
    }

    @Test
    void agregarValoracion_DeberiaGuardarValoracion() {
        Receta receta = new Receta();
        receta.setId(1L);
        Valoracion valoracion = new Valoracion();
        valoracion.setReceta(receta);

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));

        recetaService.agregarValoracion(1L, valoracion);

        verify(valoracionRepository).save(valoracion);
    }

    @Test
    void actualizarPromedioPuntuacion_DeberiaActualizarPromedio() {
        Receta receta = new Receta();
        receta.setId(1L);
        Double promedio = 4.5;

        when(valoracionRepository.promedioValoracionesPorReceta(1L)).thenReturn(promedio);

        recetaService.actualizarPromedioPuntuacion(receta);

        assertEquals(promedio, receta.getPromedioPuntuacion());
        verify(recetaRepository).save(receta);
    }

    @Test
    void obtenerRecetasPorUsuario_DeberiaRetornarRecetasDeUsuario() {
        User usuario = new User();
        List<Receta> recetas = Arrays.asList(new Receta(), new Receta());

        when(recetaRepository.findByAutorOrderByFechaCreacionDesc(usuario)).thenReturn(recetas);

        List<Receta> resultado = recetaService.obtenerRecetasPorUsuario(usuario);

        assertEquals(2, resultado.size());
        verify(recetaRepository).findByAutorOrderByFechaCreacionDesc(usuario);
    }

    @Test
    void eliminarReceta_UsuarioAutorizado_DeberiaEliminarReceta() {
        User usuario = new User();
        usuario.setId(1L);
        
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setAutor(usuario);
        receta.setMedias(new ArrayList<>());

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(authentication.getPrincipal()).thenReturn(usuario);

        recetaService.eliminarReceta(1L, authentication);

        verify(recetaRepository).delete(receta);
    }

    @Test
    void eliminarReceta_UsuarioNoAutorizado_DeberiaLanzarExcepcion() {
        User autor = new User();
        autor.setId(1L);
        
        User otroUsuario = new User();
        otroUsuario.setId(2L);
        
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setAutor(autor);

        when(recetaRepository.findById(1L)).thenReturn(Optional.of(receta));
        when(authentication.getPrincipal()).thenReturn(otroUsuario);

        assertThrows(SecurityException.class, () -> 
            recetaService.eliminarReceta(1L, authentication)
        );
    }
}
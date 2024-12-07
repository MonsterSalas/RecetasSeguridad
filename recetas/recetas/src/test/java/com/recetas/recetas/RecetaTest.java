package com.recetas.recetas;

import org.junit.jupiter.api.Test;

import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class RecetaTest {

    @Test
    void crearReceta_DebeInicializarCamposCorrectamente() {
        Receta receta = new Receta();
        receta.setTitulo("Test Receta");
        receta.setDescripcion("Descripción test");
        receta.setIngredientes("Ingredientes test");
        receta.setInstrucciones("Instrucciones test");

        User autor = new User();
        autor.setId(1L);
        receta.setAutor(autor);

        assertEquals("Test Receta", receta.getTitulo());
        assertEquals("Descripción test", receta.getDescripcion());
        assertEquals("Ingredientes test", receta.getIngredientes());
        assertEquals("Instrucciones test", receta.getInstrucciones());
        assertEquals(autor, receta.getAutor());
        assertFalse(receta.isPopular());
        assertEquals(0.0, receta.getPromedioPuntuacion());
        assertNull(receta.getMedias());
    }


    @Test
    void listas_DebenInicializarseVacias() {
        Receta receta = new Receta();
        receta.setMedias(new ArrayList<>());
        receta.setComentarios(new ArrayList<>());
        receta.setValoraciones(new ArrayList<>());

        assertTrue(receta.getMedias().isEmpty());
        assertTrue(receta.getComentarios().isEmpty());
        assertTrue(receta.getValoraciones().isEmpty());
    }
}
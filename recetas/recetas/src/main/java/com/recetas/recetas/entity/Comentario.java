package com.recetas.recetas.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String contenido;
    private LocalDateTime fechaCreacion;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User autor;
    
    @ManyToOne
    @JoinColumn(name = "receta_id")
    private Receta receta;
}
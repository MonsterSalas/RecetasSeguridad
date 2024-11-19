package com.recetas.recetas.entity;

import org.hibernate.annotations.Cascade;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Valoracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int puntuacion; // 1-5 estrellas
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User usuario;
    
    @ManyToOne  
    @JoinColumn(name = "receta_id")
    private Receta receta;
}
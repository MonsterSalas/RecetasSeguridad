package com.recetas.recetas.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class RecetaMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String tipo; // "IMAGE" o "VIDEO"
    private String url;
    private String nombreArchivo;
    
    @ManyToOne
    @JoinColumn(name = "receta_id")
    private Receta receta;
}

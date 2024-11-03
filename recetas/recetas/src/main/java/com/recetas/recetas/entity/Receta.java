package com.recetas.recetas.entity;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "recetas")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String cocina;
    private String ingredientes;
    private String pais;
    private String dificultad;
    private String tiempoPreparacion;
    private String tiempoCoccion;
    private String instrucciones;
    private String urlImagen;
    private boolean popular;
}
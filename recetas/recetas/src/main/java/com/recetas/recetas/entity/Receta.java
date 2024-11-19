package com.recetas.recetas.entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "recetas")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String ingredientes;

    @Column(columnDefinition = "TEXT")
    private String instrucciones;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User autor;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean popular = false;  // Valor por defecto false

    private double promedioPuntuacion = 0.0;

    private String compartirUrl;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<RecetaMedia> medias;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL)
    private List<Valoracion> valoraciones;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
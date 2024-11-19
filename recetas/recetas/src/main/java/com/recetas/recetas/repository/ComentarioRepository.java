package com.recetas.recetas.repository;

import com.recetas.recetas.entity.Comentario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByRecetaIdOrderByFechaCreacionDesc(Long recetaId);
}
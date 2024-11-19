package com.recetas.recetas.repository;

import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    @Query("SELECT COALESCE(AVG(v.puntuacion), 0) FROM Valoracion v WHERE v.receta = :receta")
    Double promedioValoracionesPorReceta(@Param("receta") Long receta);

    boolean existsByRecetaIdAndUsuarioId(Long recetaId, Long usuarioId);
}
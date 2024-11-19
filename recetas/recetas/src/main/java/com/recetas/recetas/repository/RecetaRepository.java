package com.recetas.recetas.repository;
import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    List<Receta> findTop10ByOrderByFechaCreacionDesc();
    List<Receta> findTop10ByOrderByPromedioPuntuacionDesc();
    List<Receta> findByAutorOrderByFechaCreacionDesc(User autor);
    
    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.receta.id = :recetaId")
    Double promedioValoracionesPorReceta(Long recetaId);
}
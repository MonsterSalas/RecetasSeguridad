package com.recetas.recetas.repository;

import com.recetas.recetas.entity.RecetaMedia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaMediaRepository extends JpaRepository<RecetaMedia, Long> {
    List<RecetaMedia> findByRecetaId(Long recetaId);
}

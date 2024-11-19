package com.recetas.recetas.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recetas.recetas.entity.Comentario;
import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.User;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UserRepository;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private UserRepository userRepository;

    public void guardarComentario(Long recetaId, String contenido, String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            Comentario comentario = new Comentario();
            comentario.setContenido(contenido);
            comentario.setFechaCreacion(LocalDateTime.now());
            comentario.setAutor(user);
    
            // Buscar la entidad Receta
            Optional<Receta> optionalReceta = recetaRepository.findById(recetaId);
            if (optionalReceta.isPresent()) {
                comentario.setReceta(optionalReceta.get());
            } else {
                System.out.println("Receta no encontrada");
                return; // Salir si la receta no existe
            }
    
            comentarioRepository.save(comentario);
        } else {
            System.out.println("Usuario no encontrado");
        }
    }
    
}

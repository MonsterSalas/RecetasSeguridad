package com.recetas.recetas.service;

import com.recetas.recetas.controllers.AuthController;
import com.recetas.recetas.entity.*;
import com.recetas.recetas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class RecetaService {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @Autowired
    private RecetaRepository recetaRepository;
    
    @Autowired
    private RecetaMediaRepository mediaRepository;
    
    @Autowired
    private ComentarioRepository comentarioRepository;
    
    @Autowired
    private ValoracionRepository valoracionRepository;

    // Método que faltaba
    public List<Receta> obtenerTodasRecetas() {
        return recetaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion"));
    }

    // Método para obtener recetas recientes
    public List<Receta> obtenerRecetasRecientes() {
        return recetaRepository.findTop10ByOrderByFechaCreacionDesc();
    }

    // Método para obtener recetas populares
    public List<Receta> obtenerRecetasPopulares() {
        return recetaRepository.findTop10ByOrderByPromedioPuntuacionDesc();
    }
    
    public Receta crearReceta(Receta receta, List<MultipartFile> archivos) throws IOException {
        receta.setFechaCreacion(LocalDateTime.now());
        receta.setPopular(false);
        receta.setPromedioPuntuacion(0.0);
        receta.setCompartirUrl(UUID.randomUUID().toString());
        
        // Validar campos requeridos
        if (receta.getTitulo() == null || receta.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es requerido");
        }
        
        // Crear directorio si no existe
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // Guardar la receta
        Receta recetaGuardada = recetaRepository.save(receta);
        
        // Procesar archivos multimedia
        if (archivos != null && !archivos.isEmpty()) {
            for (MultipartFile archivo : archivos) {
                if (!archivo.isEmpty()) {
                    String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
                    Path rutaCompleta = uploadDir.resolve(nombreArchivo);
                    
                    Files.write(rutaCompleta, archivo.getBytes());
                    
                    RecetaMedia media = new RecetaMedia();
                    media.setReceta(recetaGuardada);
                    media.setNombreArchivo(nombreArchivo);
                    media.setUrl("/uploads/" + nombreArchivo);
                    media.setTipo(determinarTipoArchivo(archivo.getContentType()));
                    
                    mediaRepository.save(media);
                }
            }
        }
        
        return recetaGuardada;
    }
    
    public Receta obtenerReceta(Long id) {
        return recetaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
    }
    
    public void agregarComentario(Long recetaId, Comentario comentario) {
        Receta receta = obtenerReceta(recetaId);
        comentario.setReceta(receta);
        comentario.setFechaCreacion(LocalDateTime.now());
        comentarioRepository.save(comentario);
    }
    
    public void agregarValoracion(Long recetaId, Valoracion valoracion) {
        Optional<Receta> receta = recetaRepository.findById(valoracion.getReceta().getId());
            // Guardar la valoración
            valoracionRepository.save(valoracion);
    }
    
    public void actualizarPromedioPuntuacion(Receta receta) {
        logger.debug("Actualizando promedio de puntuaciones para la receta: {}", receta.getId());

        Double promedio = valoracionRepository.promedioValoracionesPorReceta(receta.getId());
        logger.info("Promedio obtenido: {}", promedio);

        receta.setPromedioPuntuacion(promedio);
        recetaRepository.save(receta);
    }
    
    
    private String determinarTipoArchivo(String contentType) {
        if (contentType.startsWith("image/")) {
            return "IMAGE";
        } else if (contentType.startsWith("video/")) {
            return "VIDEO";
        }
        throw new IllegalArgumentException("Tipo de archivo no soportado: " + contentType);
    }

    public List<Receta> obtenerRecetasPorUsuario(User usuario) {
        return recetaRepository.findByAutorOrderByFechaCreacionDesc(usuario);
    }

    public void eliminarReceta(Long id, Authentication auth) {
        Receta receta = obtenerReceta(id);
        User usuario = (User) auth.getPrincipal();
        
        if (!receta.getAutor().getId().equals(usuario.getId())) {
            throw new SecurityException("No tienes permiso para eliminar esta receta");
        }
        
        // Eliminar archivos físicos
        if (receta.getMedias() != null) {
            for (RecetaMedia media : receta.getMedias()) {
                try {
                    Files.deleteIfExists(Paths.get(uploadPath, media.getNombreArchivo()));
                } catch (IOException e) {
                    // Log error but continue
                }
            }
        }
        
        recetaRepository.delete(receta);
    }
}
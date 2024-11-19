package com.recetas.recetas.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recetas.recetas.entity.Comentario;
import com.recetas.recetas.entity.Receta;
import com.recetas.recetas.entity.User;
import com.recetas.recetas.entity.Valoracion;
import com.recetas.recetas.repository.ComentarioRepository;
import com.recetas.recetas.repository.RecetaRepository;
import com.recetas.recetas.repository.UserRepository;
import com.recetas.recetas.service.ComentarioService;
import com.recetas.recetas.service.RecetaService;
import com.recetas.recetas.service.UserService;

@Controller
@RequestMapping("/receta")
public class RecetaController {
    private static final Logger logger = LoggerFactory.getLogger(RecetaController.class);

    @Autowired
    private RecetaService recetaService;

    @Autowired
    private UserService userService; // Servicio para obtener el usuario de la base de datos

   @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        logger.debug("Mostrando formulario de crear receta");
        model.addAttribute("receta", new Receta());
        return "crear-receta";
    }

    @PostMapping("/crear")
    public String procesarCrearReceta(@ModelAttribute Receta receta,
            @RequestParam("archivos") List<MultipartFile> archivos,
            Authentication authentication) {
        try {
            logger.debug("Procesando creación de receta: {}", receta.getTitulo());

            // Obtener el usuario de la base de datos usando el username
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User usuario = userService.findByUsername(userDetails.getUsername());

            if (usuario == null) {
                logger.error("No se encontró el usuario en la base de datos");
                return "redirect:/receta/crear?error=true";
            }

            receta.setAutor(usuario);
            receta.setFechaCreacion(LocalDateTime.now());

            Receta recetaCreada = recetaService.crearReceta(receta, archivos);
            logger.info("Receta creada exitosamente con ID: {}", recetaCreada.getId());

            return "redirect:/receta/" + recetaCreada.getId();
        } catch (Exception e) {
            logger.error("Error al crear receta: ", e);
            return "redirect:/receta/crear?error=true";
        }
    }

    // Ver detalle de receta
    @GetMapping("/{id}")
    public String verReceta(@PathVariable Long id, Model model) {
        try {
            Receta receta = recetaService.obtenerReceta(id);
            model.addAttribute("receta", receta);
            return "detalle-receta";
        } catch (Exception e) {
            logger.error("Error al obtener receta: ", e);
            return "redirect:/recetas?error=true";
        }
    }

    @GetMapping("/mis-recetas")
    public String misRecetas(Model model, Authentication authentication) {
        try {
            // Obtenemos el usuario autenticado
            String username = authentication.getName();
            User usuario = userService.findByUsername(username);

            List<Receta> misRecetas = recetaService.obtenerRecetasPorUsuario(usuario);
            model.addAttribute("misRecetas", misRecetas);
            return "mis-recetas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las recetas");
            return "redirect:/home";
        }
    }

    @PostMapping("/eliminar/{id}") // Cambiado a POST para mejor compatibilidad
    public String eliminarReceta(@PathVariable Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            recetaService.eliminarReceta(id, authentication);
            redirectAttributes.addFlashAttribute("mensaje", "Receta eliminada exitosamente");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta receta");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la receta");
        }
        return "redirect:/receta/mis-recetas";
    }

    @PostMapping("/{id}/comentar")
    public ResponseEntity<String> comentarReceta(@PathVariable Long id, @RequestParam String contenido, Authentication authentication) {
        String username = authentication.getName();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    
        User user = optionalUser.get();
    
        Optional<Receta> optionalReceta = recetaRepository.findById(id);
        if (optionalReceta.isEmpty()) {
            return ResponseEntity.status(404).body("Receta no encontrada");
        }
    
        Receta receta = optionalReceta.get();
    
        Comentario comentario = new Comentario();
        comentario.setContenido(contenido);
        comentario.setFechaCreacion(LocalDateTime.now());
        comentario.setAutor(user);
        comentario.setReceta(receta);
    
        comentarioRepository.save(comentario);
    
        return ResponseEntity.ok("Comentario guardado con éxito");
    }
    @PostMapping("/{id}/valorar")
    public ResponseEntity<String> valorarReceta(@PathVariable Long id, @RequestParam int puntuacion, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User usuario = userService.findByUsername(userDetails.getUsername());

            Receta receta = recetaService.obtenerReceta(id);
            Valoracion valoracion = new Valoracion();
            valoracion.setUsuario(usuario);
            valoracion.setReceta(receta);
            valoracion.setPuntuacion(puntuacion);

            receta.getValoraciones().add(valoracion);
            recetaService.actualizarPromedioPuntuacion(receta);

            return ResponseEntity.ok("Valoración guardada exitosamente");
        } catch (Exception e) {
            logger.error("Error al guardar valoración", e);
            return ResponseEntity.status(500).body("Error al guardar valoración");
        }
    }
}

package com.recetas.recetas.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.recetas.recetas.entity.Receta;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/recetas")
    public String index(Model model) {
        // Página principal pública
        List<Receta> recetasRecientes = Arrays.asList(
            crearReceta("Pasta Carbonara", "Italiana", "Fácil", "pasta.jpg", true),
            crearReceta("Sushi Roll", "Japonesa", "Medio", "sushi.jpg", true),
            crearReceta("Paella", "Española", "Difícil", "paella.jpg", true)
        );

        List<String> banners = Arrays.asList(
            "¡Nueva línea de utensilios de cocina!",
            "Descuentos en productos gourmet",
            "Clases de cocina disponibles"
        );

        model.addAttribute("recetasRecientes", recetasRecientes);
        model.addAttribute("recetasPopulares", recetasRecientes);
        model.addAttribute("banners", banners);
        return "index";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        
        Cookie[] cookies = request.getCookies();
        String jwt = "No se encontró token JWT";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT-TOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    logger.debug("JWT encontrado en cookie: {}", jwt);
                    break;
                }
            }
        }
        model.addAttribute("jwt", jwt);
        return "home";
    }

    @GetMapping("/buscar")
    public String buscarRecetas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String cocina,
            @RequestParam(required = false) String ingredientes,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String dificultad,
            Model model) {
        
        List<Receta> resultados = Arrays.asList(
            crearReceta("Pasta Carbonara", "Italiana", "Fácil", "pasta.jpg", false),
            crearReceta("Sushi Roll", "Japonesa", "Medio", "sushi.jpg", false),
            crearReceta("Paella", "Española", "Difícil", "paella.jpg", false)
        );

        model.addAttribute("recetas", resultados);
        return "buscar";
    }

    @GetMapping("/receta/{id}")
    public String detalleReceta(@PathVariable Long id, Model model, HttpServletRequest request) {
        Receta receta = crearDetalleReceta();
        model.addAttribute("receta", receta);
        return "detalle-receta";
    }

    private Receta crearReceta(String nombre, String cocina, String dificultad, String urlImagen, boolean popular) {
        Receta receta = new Receta();
        receta.setNombre(nombre);
        receta.setCocina(cocina);
        receta.setDificultad(dificultad);
        receta.setUrlImagen(urlImagen);
        receta.setPopular(popular);
        return receta;
    }

    // Agregamos el método que faltaba
    private Receta crearDetalleReceta() {
        Receta receta = new Receta();
        receta.setNombre("Pasta Carbonara");
        receta.setCocina("Italiana");
        receta.setDificultad("Medio");
        receta.setTiempoPreparacion("20 minutos");
        receta.setTiempoCoccion("15 minutos");
        receta.setIngredientes("Pasta, Huevos, Panceta, Queso Parmesano, Pimienta negra");
        receta.setInstrucciones("1. Cocinar la pasta al dente en agua con sal\n" +
                              "2. Mientras tanto, batir los huevos con el queso rallado\n" +
                              "3. Dorar la panceta en una sartén\n" +
                              "4. Mezclar la pasta con la panceta\n" +
                              "5. Agregar la mezcla de huevos y queso\n" +
                              "6. Servir con pimienta negra recién molida");
        receta.setUrlImagen("carbonara.jpg");
        receta.setPopular(true);
        receta.setPais("Italia");
        return receta;
    }
}

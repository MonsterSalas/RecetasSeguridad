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
            crearReceta(1L, "Pasta Carbonara", "Italiana", "Fácil", "pasta.jpg", true),
            crearReceta(2L, "Sushi Roll", "Japonesa", "Medio", "sushi.jpg", true),
            crearReceta(3L, "Paella", "Española", "Difícil", "paella.jpg", true)
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

    @GetMapping("/buscar")
    public String buscarRecetas(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String cocina,
            @RequestParam(required = false) String ingredientes,
            @RequestParam(required = false) String pais,
            @RequestParam(required = false) String dificultad,
            Model model) {
        
        List<Receta> resultados = Arrays.asList(
            crearReceta(1L, "Pasta Carbonara", "Italiana", "Fácil", "pasta.jpg", false),
            crearReceta(2L, "Sushi Roll", "Japonesa", "Medio", "sushi.jpg", false),
            crearReceta(3L, "Paella", "Española", "Difícil", "paella.jpg", false)
        );

        model.addAttribute("recetas", resultados);
        return "buscar";
    }

    @GetMapping("/receta/{id}")
    public String detalleReceta(@PathVariable Long id, Model model, HttpServletRequest request) {
        // Simular diferentes recetas según el ID
        Receta receta;
        switch (id.intValue()) {
            case 1:
                receta = crearDetalleRecetaCarbonara();
                break;
            case 2:
                receta = crearDetalleRecetaSushi();
                break;
            case 3:
                receta = crearDetalleRecetaPaella();
                break;
            default:
                receta = crearDetalleRecetaCarbonara(); // Por defecto
        }
        model.addAttribute("receta", receta);
        return "detalle-receta";
    }

    private Receta crearReceta(Long id, String nombre, String cocina, String dificultad, String urlImagen, boolean popular) {
        Receta receta = new Receta();
        receta.setId(id);
        receta.setNombre(nombre);
        receta.setCocina(cocina);
        receta.setDificultad(dificultad);
        receta.setUrlImagen(urlImagen);
        receta.setPopular(popular);
        return receta;
    }

    private Receta crearDetalleRecetaCarbonara() {
        Receta receta = new Receta();
        receta.setId(1L);
        receta.setNombre("Pasta Carbonara");
        receta.setCocina("Italiana");
        receta.setDificultad("Medio");
        receta.setTiempoPreparacion("20 minutos");
        receta.setTiempoCoccion("15 minutos");
        receta.setIngredientes("Pasta, Huevos, Panceta, Queso Parmesano, Pimienta negra");
        receta.setInstrucciones("Cocinar la pasta al dente en agua con sal\n" +
                              "Mientras tanto, batir los huevos con el queso rallado\n" +
                              "Dorar la panceta en una sartén\n" +
                              "Mezclar la pasta con la panceta\n" +
                              "Agregar la mezcla de huevos y queso\n" +
                              "Servir con pimienta negra recién molida");
        receta.setUrlImagen("carbonara.jpg");
        receta.setPopular(true);
        receta.setPais("Italia");
        return receta;
    }

    private Receta crearDetalleRecetaSushi() {
        Receta receta = new Receta();
        receta.setId(2L);
        receta.setNombre("Sushi Roll");
        receta.setCocina("Japonesa");
        receta.setDificultad("Medio");
        receta.setTiempoPreparacion("30 minutos");
        receta.setTiempoCoccion("10 minutos");
        receta.setIngredientes("Arroz de sushi, Alga nori, Salmón, Aguacate, Pepino, Vinagre de arroz");
        receta.setInstrucciones("Preparar el arroz de sushi\n" +
                              "Colocar el alga nori sobre la esterilla\n" +
                              "Extender el arroz sobre el alga\n" +
                              "Colocar los ingredientes\n" +
                              "Enrollar con la esterilla\n" +
                              "Cortar en piezas");
        receta.setUrlImagen("sushi.jpg");
        receta.setPopular(true);
        receta.setPais("Japón");
        return receta;
    }

    private Receta crearDetalleRecetaPaella() {
        Receta receta = new Receta();
        receta.setId(3L);
        receta.setNombre("Paella Valenciana");
        receta.setCocina("Española");
        receta.setDificultad("Difícil");
        receta.setTiempoPreparacion("45 minutos");
        receta.setTiempoCoccion("30 minutos");
        receta.setIngredientes("Arroz bomba, Azafrán, Pollo, Conejo, Judías verdes, Garrofón, Pimentón");
        receta.setInstrucciones("Sofreír la carne\n" +
                              "Añadir las verduras\n" +
                              "Incorporar el pimentón y el tomate\n" +
                              "Agregar el arroz y el caldo con azafrán\n" +
                              "Cocinar a fuego fuerte 10 minutos\n" +
                              "Bajar el fuego y cocinar 8 minutos más\n" +
                              "Reposar");
        receta.setUrlImagen("paella.jpg");
        receta.setPopular(true);
        receta.setPais("España");
        return receta;
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
}

package com.recetas.recetas.controllers;

import com.recetas.recetas.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
public class HomeController {
    
    @Autowired
    private RecetaService recetaService;

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("recetasRecientes", recetaService.obtenerRecetasRecientes());
        model.addAttribute("recetasPopulares", recetaService.obtenerRecetasPopulares());
        
        model.addAttribute("banners", Arrays.asList(
            "¡Nueva línea de utensilios de cocina!",
            "Descuentos en productos gourmet",
            "Clases de cocina disponibles"
        ));
        
        return "index";
    }

    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        
        // Obtener recetas para mostrar en el dashboard
        model.addAttribute("misRecetas", recetaService.obtenerTodasRecetas());
        
        // Manejar JWT
        Cookie[] cookies = request.getCookies();
        String jwt = "No se encontró token JWT";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT-TOKEN".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        model.addAttribute("jwt", jwt);
        return "home";
    }
}
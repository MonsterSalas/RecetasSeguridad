package com.recetas.recetas.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.Cookie;
import com.recetas.recetas.security.JwtUtil;
import com.recetas.recetas.service.CookieService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CookieService cookieService;

    @GetMapping("/login")
    public String showLoginForm(HttpServletRequest request) {
        // Verificar si existe un JWT válido
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT-TOKEN".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    // Verificar si el token es válido
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                        logger.debug("Usuario ya autenticado, redirigiendo a home");
                        return "redirect:/home";
                    }
                }
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            Model model) {
        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // Establecer la autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar el token JWT
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            logger.debug("JWT generado: {}", jwt);

            // Guardar el token en una cookie
            cookieService.createJwtCookie(jwt, response);

            return "redirect:/home";
        } catch (AuthenticationException e) {
            logger.error("Error de autenticación: ", e);
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        cookieService.clearJwtCookie(response);
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }
}
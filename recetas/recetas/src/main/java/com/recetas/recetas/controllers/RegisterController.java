package com.recetas.recetas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.recetas.recetas.entity.User;
import com.recetas.recetas.repository.UserRepository;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String confirmPassword,
                             Model model) {
        
        // Validaciones
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register";
        }

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "El nombre de usuario ya está en uso");
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "El email ya está registrado");
            return "register";
        }

        // Crear nuevo usuario
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);

        model.addAttribute("success", "Registro exitoso. Por favor, inicia sesión.");
        return "register";
    }
}
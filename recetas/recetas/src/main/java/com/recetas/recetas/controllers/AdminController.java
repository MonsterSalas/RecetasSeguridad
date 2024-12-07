package com.recetas.recetas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.recetas.recetas.entity.User;
import com.recetas.recetas.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;
    String users = "redirect:/admin/users";
    
    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }
    
    @PostMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, @ModelAttribute User user) {
        userService.updateUser(id, user);
        return users;
    }
    
    @PostMapping("/users/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return users;
    }
    
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return users;
    }
}
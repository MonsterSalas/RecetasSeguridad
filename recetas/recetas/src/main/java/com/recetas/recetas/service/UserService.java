package com.recetas.recetas.service;

import com.recetas.recetas.entity.User;
import com.recetas.recetas.repository.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        userRepository.save(user);
    }
    
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setSuspended(!user.isSuspended());
        userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
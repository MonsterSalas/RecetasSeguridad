package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import com.recetas.recetas.controllers.RegisterController;
import com.recetas.recetas.entity.User;
import com.recetas.recetas.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegisterControllerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Model model;
    @InjectMocks
    private RegisterController registerController;

    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "password123";

    @Test
    void testShowRegistrationForm() {
        assertEquals("register", registerController.showRegistrationForm());
    }

    @Test
    void testSuccessfulRegistration() {
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");

        String viewName = registerController.registerUser(
            TEST_USERNAME, 
            TEST_EMAIL,
            TEST_PASSWORD,
            TEST_PASSWORD,
            model
        );

        verify(userRepository).save(any(User.class));
        verify(model).addAttribute("success", "Registro exitoso. Por favor, inicia sesión.");
        assertEquals("register", viewName);
    }

    @Test
    void testPasswordMismatch() {
        String viewName = registerController.registerUser(
            TEST_USERNAME,
            TEST_EMAIL,
            TEST_PASSWORD,
            "differentPassword",
            model
        );

        verify(model).addAttribute("error", "Las contraseñas no coinciden");
        assertEquals("register", viewName);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDuplicateUsername() {
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(true);

        String viewName = registerController.registerUser(
            TEST_USERNAME,
            TEST_EMAIL,
            TEST_PASSWORD,
            TEST_PASSWORD,
            model
        );

        verify(model).addAttribute("error", "El nombre de usuario ya está en uso");
        assertEquals("register", viewName);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDuplicateEmail() {
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);

        String viewName = registerController.registerUser(
            TEST_USERNAME,
            TEST_EMAIL,
            TEST_PASSWORD,
            TEST_PASSWORD,
            model
        );

        verify(model).addAttribute("error", "El email ya está registrado");
        assertEquals("register", viewName);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUserCreationWithCorrectRole() {
        when(userRepository.existsByUsername(TEST_USERNAME)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");

        registerController.registerUser(
            TEST_USERNAME,
            TEST_EMAIL,
            TEST_PASSWORD,
            TEST_PASSWORD,
            model
        );

        verify(userRepository).save(argThat(user -> 
            user.getRole().equals("USER") &&
            user.getUsername().equals(TEST_USERNAME) &&
            user.getEmail().equals(TEST_EMAIL)
        ));
    }
}
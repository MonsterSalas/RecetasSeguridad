package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import com.recetas.recetas.controllers.AdminController;
import com.recetas.recetas.entity.User;
import com.recetas.recetas.service.UserService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    private User testUser;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
    }

    @Test
    void testUserManagement() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        when(userService.findAllUsers()).thenReturn(users);

        // Act
        String viewName = adminController.userManagement(model);

        // Assert
        verify(model).addAttribute("users", users);
        assertEquals("admin/users", viewName);
    }

    @Test
    void testEditUser() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setId(userId);

        // Act
        String viewName = adminController.editUser(userId, updatedUser);

        // Assert
        verify(userService).updateUser(userId, updatedUser);
        assertEquals("redirect:/admin/users", viewName);
    }

    @Test
    void testToggleStatus() {
        // Act
        String viewName = adminController.toggleStatus(userId);

        // Assert
        verify(userService).toggleUserStatus(userId);
        assertEquals("redirect:/admin/users", viewName);
    }

    @Test
    void testDeleteUser() {
        // Act
        String viewName = adminController.deleteUser(userId);

        // Assert
        verify(userService).deleteUser(userId);
        assertEquals("redirect:/admin/users", viewName);
    }

    @Test
    void testUserManagementWithEmptyList() {
        // Arrange
        when(userService.findAllUsers()).thenReturn(Arrays.asList());

        // Act
        String viewName = adminController.userManagement(model);

        // Assert
        verify(model).addAttribute("users", Arrays.asList());
        assertEquals("admin/users", viewName);
    }

    @Test
    void testEditUserWithInvalidId() {
        // Arrange
        User updatedUser = new User();
        doThrow(new RuntimeException("User not found")).when(userService).updateUser(userId, updatedUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            adminController.editUser(userId, updatedUser);
        });
    }

    @Test
    void testToggleStatusWithInvalidId() {
        // Arrange
        doThrow(new RuntimeException("User not found")).when(userService).toggleUserStatus(userId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            adminController.toggleStatus(userId);
        });
    }

    @Test
    void testDeleteUserWithInvalidId() {
        // Arrange
        doThrow(new RuntimeException("User not found")).when(userService).deleteUser(userId);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            adminController.deleteUser(userId);
        });
    }
}
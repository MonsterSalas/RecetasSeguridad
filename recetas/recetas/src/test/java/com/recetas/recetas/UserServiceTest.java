package com.recetas.recetas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.recetas.recetas.entity.User;
import com.recetas.recetas.repository.UserRepository;
import com.recetas.recetas.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@test.com");
        testUser.setRole("USER");
        testUser.setSuspended(false);
    }

    @Test
    void findByUsername_CuandoExisteUsuario_DebeRetornarUsuario() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        User result = userService.findByUsername("testUser");
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void findByUsername_CuandoNoExisteUsuario_DebeLanzarExcepcion() {
        when(userRepository.findByUsername("noExiste")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.findByUsername("noExiste"));
    }

    @Test
    void findAllUsers_DebeRetornarListaDeUsuarios() {
        List<User> users = Arrays.asList(testUser, new User());
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.findAllUsers();
        assertEquals(2, result.size());
    }

    @Test
    void updateUser_CuandoExisteUsuario_DebeActualizarCorrectamente() {
        User updatedUser = new User();
        updatedUser.setUsername("newUsername");
        updatedUser.setEmail("new@email.com");
        updatedUser.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser(1L, updatedUser);

        assertEquals("newUsername", testUser.getUsername());
        assertEquals("new@email.com", testUser.getEmail());
        assertEquals("ADMIN", testUser.getRole());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_CuandoNoExisteUsuario_DebeLanzarExcepcion() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> 
            userService.updateUser(1L, new User()));
    }

    @Test
    void toggleUserStatus_CuandoExisteUsuario_DebeAlternarEstado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
         
        userService.toggleUserStatus(1L);
        
        assertTrue(testUser.isSuspended());
        verify(userRepository).save(testUser);
    }

    @Test
    void toggleUserStatus_CuandoNoExisteUsuario_DebeLanzarExcepcion() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> 
            userService.toggleUserStatus(1L));
    }

    @Test
    void deleteUser_DebeEliminarUsuario() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}
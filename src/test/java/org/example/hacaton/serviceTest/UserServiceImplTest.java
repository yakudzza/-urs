package org.example.hacaton.serviceTest;


import org.example.hacaton.exception.CustomException;
import org.example.hacaton.model.user.User;
import org.example.hacaton.repository.UserRepository;
import org.example.hacaton.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        var result = userService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, ((User) result).getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void loadUserByUsername_UserDoesNotExist() {
        // Arrange
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername(email));
        assertEquals("Пользователь с таким email не найден", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void createUser_Success() {
        // Arrange
        User user = new User();
        user.setEmail("newuser@example.com");

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.create(user);

        // Assert
        assertNotNull(result);
        assertEquals("newuser@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByEmail_UserExists() {
        // Arrange
        String email = "found@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_UserDoesNotExist() {
        // Arrange
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(CustomException.class, () ->
                userService.findByEmail(email));
        assertEquals("Пользователь с таким email не существует", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getUserById_UserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_UserDoesNotExist() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.getUserById(userId));
        assertEquals("Пользователя с таким id нет", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}
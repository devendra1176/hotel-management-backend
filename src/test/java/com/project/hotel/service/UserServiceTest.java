package com.project.hotel.service;

import com.project.hotel.dto.UserRequestDTO;
import com.project.hotel.dto.UserResponseDTO;
import com.project.hotel.entity.Role;
import com.project.hotel.entity.User;
import com.project.hotel.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {

        // ARRANGE
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Devendra");
        dto.setEmail("dev@mail.com");
        dto.setPassword("123456");

        when(userRepository.findByEmail("dev@mail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Devendra");
        savedUser.setEmail("dev@mail.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // ACT
        UserResponseDTO response = userService.createUser(dto);

        // ASSERT
        assertEquals(1L, response.getId());
        assertEquals("Devendra", response.getName());
        assertEquals("dev@mail.com", response.getEmail());
        assertEquals("USER", response.getRole());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        // ARRANGE
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Devendra");
        dto.setEmail("dev@mail.com");
        dto.setPassword("123456");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("dev@mail.com");

        when(userRepository.findByEmail("dev@mail.com"))
                .thenReturn(Optional.of(existingUser));

        // ACT + ASSERT
        assertThrows(
                RuntimeException.class,
                () -> userService.createUser(dto)
        );

        // VERIFY
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldAssignRoleUserByDefault() {

        // ARRANGE
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Devendra");
        dto.setEmail("dev@mail.com");
        dto.setPassword("123456");

        when(userRepository.findByEmail("dev@mail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Devendra");
        savedUser.setEmail("dev@mail.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // ACT
        userService.createUser(dto);

        // ASSERT using ArgumentCaptor
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertEquals(Role.USER, userToSave.getRole());
    }

    @Test
    void createUser_shouldEncodePassword() {

        // ARRANGE
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("Devendra");
        dto.setEmail("dev@mail.com");
        dto.setPassword("123456");

        when(userRepository.findByEmail("dev@mail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Devendra");
        savedUser.setEmail("dev@mail.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // ACT
        userService.createUser(dto);

        // ASSERT
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertEquals("encoded-password", userToSave.getPassword());
        verify(passwordEncoder, times(1)).encode("123456");
    }
}
package com.project.hotel.security;

import com.project.hotel.entity.Role;
import com.project.hotel.entity.User;
import com.project.hotel.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {

        // ARRANGE
        User user = new User();
        user.setId(1L);
        user.setName("Devendra");
        user.setEmail("dev@mail.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);

        when(userRepository.findByEmail("dev@mail.com"))
                .thenReturn(Optional.of(user));

        // ACT
        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("dev@mail.com");

        // ASSERT
        assertEquals("dev@mail.com", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))
        );

        verify(userRepository, times(1)).findByEmail("dev@mail.com");
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {

        // ARRANGE
        when(userRepository.findByEmail("missing@mail.com"))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing@mail.com")
        );

        verify(userRepository, times(1)).findByEmail("missing@mail.com");
    }
}
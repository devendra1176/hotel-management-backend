package com.project.hotel.service;

import com.project.hotel.dto.UserRequestDTO;
import com.project.hotel.dto.UserResponseDTO;
import com.project.hotel.entity.Role;
import com.project.hotel.entity.User;
import com.project.hotel.exception.UserNotFoundException;
import com.project.hotel.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // CREATE USER (REGISTER)
    // =========================================================
    public UserResponseDTO createUser(UserRequestDTO dto) {

        // 1. Duplicate email check
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Create Entity
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        // 3. Encode Password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 4. ALWAYS SET DEFAULT ROLE (SECURE)
        user.setRole(Role.USER);

        // 5. Save to DB
        User savedUser = userRepository.save(user);

        // 6. Return Response DTO
        return mapToResponse(savedUser);
    }

    // =========================================================
    // LOGIN (AUTHENTICATION)
    // =========================================================
    public User login(String email, String password) {

        // 1. Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Match password (ENCODED)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3. Return full user (for JWT generation)
        return user;
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    public Page<UserResponseDTO> getAllUsers(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());

        Page<User> users = userRepository.findAll(pageable);

        return users.map(this::mapToResponse);
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id)
                );

        return mapToResponse(user);
    }

    // =========================================================
    // DELETE USER
    // =========================================================
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id)
                );

        userRepository.delete(user);
    }

    // =========================================================
    // COMMON MAPPING METHOD (REUSABLE)
    // =========================================================
    private UserResponseDTO mapToResponse(User user) {

        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());

        return dto;
    }
}
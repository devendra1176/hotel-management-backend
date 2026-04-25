package com.project.hotel.config;

import com.project.hotel.entity.Role;
import com.project.hotel.entity.User;
import com.project.hotel.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        boolean adminExists = userRepository.findByEmail("admin@gmail.com").isPresent();

        if (adminExists) {
            return;
        }

        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }
}
package com.newsaggregatorapp.controller;

import java.util.Optional;

import com.newsaggregatorapp.dto.UserDTO;
import com.newsaggregatorapp.entity.User;
import com.newsaggregatorapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        String normalizedUsername = user.getUsername().trim().toLowerCase(); //  Normalize username
        if (userRepository.existsByUsername(normalizedUsername)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        user.setUsername(normalizedUsername); //  Set normalized username
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String normalizedUsername = user.getUsername().trim().toLowerCase(); //  Normalize username
        Optional<User> optionalUser = userRepository.findByUsername(normalizedUsername);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                UserDTO userDTO = new UserDTO(
                    existingUser.getId(),
                    existingUser.getUsername(),
                    existingUser.getPreferences()
                );
                return ResponseEntity.ok(userDTO);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}

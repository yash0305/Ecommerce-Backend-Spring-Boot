package com.yash.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yash.entity.User;
import com.yash.enums.Role;
import com.yash.repository.UserRepository;

import java.time.LocalDateTime;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // inject encoder


    public User registerUser(String username, String plainPassword, Role role, String email, String mobileNumber) {

        // Validate username
        if (username == null || username.isBlank() || username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }

        // Validate password
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        // Validate role
        if (role == null) {
            throw new IllegalArgumentException("Role must be provided (ADMIN, CUSTOMER, SELLER)");
        }

        // Validate email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validate mobile number
        if (mobileNumber == null || !mobileNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile number must be 10 digits");
        }

        // Check uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException("Mobile number already exists");
        }

        // Hash the password
        String hashedPassword = passwordEncoder.encode(plainPassword);

        // Create user entity
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole(role);
        user.setMobileNumber(mobileNumber);
        user.setEmail(email);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save to DB
        return userRepository.save(user);
    }





}

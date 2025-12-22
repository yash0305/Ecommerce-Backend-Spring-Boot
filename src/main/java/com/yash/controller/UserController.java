package com.yash.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yash.dto.LoginRequestDTO;
import com.yash.dto.LoginResponseDTO;
import com.yash.entity.User;
import com.yash.repository.UserRepository;
import com.yash.util.JwtUtil;

import org.springframework.web.bind.annotation.RestController;

import com.yash.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllAdmin() {
        return userRepository.findAllAdmin();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User entity) {
        try {
            userService.registerUser(
                    entity.getUsername(),
                    entity.getPassword(),
                    entity.getRole(),
                    entity.getEmail(),
                    entity.getMobileNumber()
            );
            return ResponseEntity.ok(entity.getRole() + " added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {

        User user = userRepository.findByUsername(req.getUsername());
        System.out.println("user : " + user.getUsername());

        if (user != null && passwordEncoder.matches(req.getPassword(), user.getPassword())) {

            String token = jwtUtil.generateToken(user);

            LoginResponseDTO response = new LoginResponseDTO(
                    token
            );

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body("Invalid credentials");

    }

    @GetMapping("/test")
    public String test(){
        return "test";

    }


}




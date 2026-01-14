package com.yash.controller;


import com.yash.service.RefreshTokenService;
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

import com.yash.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private RefreshTokenService refreshTokenService;

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

        if (user != null && passwordEncoder.matches(req.getPassword(), user.getPassword())) {

            String access = jwtUtil.generateAccessToken(user);
            String refresh = jwtUtil.generateRefreshToken(user);

            refreshTokenService.save(refresh, user.getUsername());

            return ResponseEntity.ok(new LoginResponseDTO(access, refresh));
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken){

        String username = refreshTokenService.getUsername(refreshToken);

        if(username == null){
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        // Sliding window: delete old
        refreshTokenService.delete(refreshToken);

        User user = userRepository.findByUsername(username);

        String newAccess = jwtUtil.generateAccessToken(user);
        String newRefresh = jwtUtil.generateRefreshToken(user);

        refreshTokenService.save(newRefresh, username);

        return ResponseEntity.ok(new LoginResponseDTO(newAccess, newRefresh));
    }

    @PostMapping("/logout")
    public void logout(@RequestBody String refreshToken){
        refreshTokenService.delete(refreshToken);
    }



    @GetMapping("/test")
    public String test(){
        return "test";

    }


}




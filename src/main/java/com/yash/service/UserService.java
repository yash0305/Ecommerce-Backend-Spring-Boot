package com.yash.service;
import com.yash.entity.EmailVerificationToken;
import com.yash.entity.SellerProfile;
import com.yash.enums.ApplicationStatus;
import com.yash.repository.EmailVerificationTokenRepository;
import com.yash.repository.SellerRepository;
import jakarta.transaction.Transactional;
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
import java.util.UUID;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private EmailService emailService;


    @Transactional
    public User registerUser(
            String username,
            String plainPassword,
            Role role,
            String email,
            String mobileNumber
    ) {


        if (username == null || username.isBlank() || username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }

        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role must be provided");
        }

        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (mobileNumber == null || !mobileNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile number must be 10 digits");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new IllegalArgumentException("Mobile number already exists");
        }

        // ---------- CREATE USER ----------
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setRole(role);
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // ---------- IF SELLER ----------
        if (role == Role.SELLER) {


            SellerProfile seller = new SellerProfile();
            seller.setUser(savedUser);
            seller.setEmailVerified(false);
            seller.setStatus(ApplicationStatus.PENDING);

            SellerProfile savedSeller = sellerRepository.save(seller);

            // Create Email Verification Token
            String token = UUID.randomUUID().toString();

            EmailVerificationToken verificationToken = new EmailVerificationToken();
            verificationToken.setToken(token);
            verificationToken.setSeller(savedSeller);
            verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

            emailVerificationTokenRepository.save(verificationToken);

            // Send verification email
            String verifyLink = "http://localhost:8080/seller/verify?token=" + token;

            emailService.sendEmail(
                    savedUser.getEmail(),
                    "Verify your Seller Account",
                    "Click the link to verify your account:\n" + verifyLink
            );
        }

        return savedUser;
    }






}

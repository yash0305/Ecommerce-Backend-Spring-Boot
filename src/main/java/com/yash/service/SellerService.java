package com.yash.service;

import com.yash.entity.EmailVerificationToken;
import com.yash.entity.SellerProfile;
import com.yash.entity.User;
import com.yash.enums.ApplicationStatus;
import com.yash.repository.EmailVerificationTokenRepository;
import com.yash.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SellerService {

    private final SellerProfileRepository sellerProfileRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    public SellerService(
            SellerProfileRepository sellerProfileRepository,
            EmailVerificationTokenRepository tokenRepository,
            EmailService emailService
    ) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public void registerSeller(SellerProfile seller) {

        // set initial values
        seller.setEmailVerified(false);
        seller.setStatus(ApplicationStatus.PENDING);

        // save seller
        SellerProfile savedSeller = sellerProfileRepository.save(seller);

        // generate verification token
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setSeller(savedSeller);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        tokenRepository.save(verificationToken);

        // send verification email
        String verifyLink = "http://localhost:8080/seller/verify?token=" + token;

        emailService.sendEmail(
                savedSeller.getUser().getEmail(),   // ✅ CORRECT
                "Verify your seller account",
                "Click to verify your email: " + verifyLink
        );
    }
}

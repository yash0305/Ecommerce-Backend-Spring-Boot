package com.yash.service;

import com.yash.entity.EmailVerificationToken;
import com.yash.entity.SellerProfile;
import com.yash.enums.ApplicationStatus;
import com.yash.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    public void registerSeller(SellerProfile seller) {

        seller.setEmailVerified(false);
        seller.setStatus(ApplicationStatus.PENDING);
        sellerRepository.save(seller);

        // generate token
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setSeller(seller);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        // send email
        String verifyLink = "http://localhost:8080/seller/verify?token=" + token;
        emailService.sendEmail(
                seller.getEmail(),
                "Verify your seller account",
                "Click to verify your email: " + verifyLink
        );
    }



}

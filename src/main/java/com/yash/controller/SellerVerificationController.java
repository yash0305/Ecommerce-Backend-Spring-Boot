package com.yash.controller;

import com.yash.entity.EmailVerificationToken;
import com.yash.entity.SellerProfile;
import com.yash.enums.ApplicationStatus;
import com.yash.repository.EmailVerificationTokenRepository;
import com.yash.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerVerificationController {

    private final EmailVerificationTokenRepository tokenRepository;
    private final SellerProfileRepository sellerRepository;

    @GetMapping("/verify")
    public ResponseEntity<?> verifySeller(@RequestParam String token) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        SellerProfile seller = verificationToken.getSeller();
        seller.setEmailVerified(true);
        seller.setStatus(ApplicationStatus.APPROVED);

        sellerRepository.save(seller);
        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok("Email verified successfully. Seller approved.");
    }
}

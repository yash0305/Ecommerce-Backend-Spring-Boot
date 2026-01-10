package com.yash.service;

import com.yash.entity.User;
import com.yash.enums.ApplicationStatus;
import com.yash.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public ResponseEntity<?> markSellerApproved(Long sellerId){

        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Seller not found"));


        if (seller.getSellerProfile() != null) {

            seller.getSellerProfile().setStatus(ApplicationStatus.APPROVED);
            userRepository.save(seller);
            return ResponseEntity.ok("Seller Approved");
        }
        else{
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Seller role required");

        }



    }

}

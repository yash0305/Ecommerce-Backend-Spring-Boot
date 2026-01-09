package com.yash.controller;


import com.yash.entity.User;
import com.yash.enums.ApplicationStatus;
import com.yash.enums.Role;
import com.yash.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("approval")
    public ResponseEntity<?> approvingSeller(@RequestParam String sellerUserName){

        User seller = userRepository.findByUsername(sellerUserName);

        if (seller == null) {
            throw new RuntimeException("Seller not found");
        }

        if (seller.getRole() == Role.SELLER) {

            seller.getSellerProfile().setStatus(ApplicationStatus.APPROVED);
            userRepository.save(seller);
            return ResponseEntity.ok("Seller Approved");
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Access denied: Seller role required");


    }

}

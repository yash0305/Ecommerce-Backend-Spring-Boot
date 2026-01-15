package com.yash.controller;


import com.yash.dto.SellerInfoDTO;
import com.yash.entity.User;
import com.yash.enums.ApplicationStatus;
import com.yash.enums.Role;
import com.yash.repository.SellerRepository;
import com.yash.repository.UserRepository;
import com.yash.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SellerRepository sellerRepository;

    @PostMapping("approval")
    public ResponseEntity<?> approvingSeller(@RequestParam Long sellerId){

        return adminService.markSellerApproved(sellerId);

    }

    @PostMapping("rejected")
    public ResponseEntity<?> rejectedSeller(@RequestParam Long sellerId){

        return adminService.markSellerRejected(sellerId);

    }

    @GetMapping("/sellers")
    public ResponseEntity<List<SellerInfoDTO>> sendAllSellerData() {

        List<SellerInfoDTO> sellers = sellerRepository.fetchSellerInfo();

        return ResponseEntity.ok(sellers);
    }


}

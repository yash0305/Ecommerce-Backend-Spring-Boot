package com.yash.controller;


import com.yash.entity.User;
import com.yash.enums.ApplicationStatus;
import com.yash.enums.Role;
import com.yash.repository.UserRepository;
import com.yash.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("approval")
    public ResponseEntity<?> approvingSeller(@RequestParam Long sellerId){

        return adminService.markSellerApproved(sellerId);



    }

}

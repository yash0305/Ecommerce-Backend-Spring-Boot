package com.yash.controller;

import com.yash.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.yash.util.OtpGenerator.generate6DigitOtp;

@RestController
@RequiredArgsConstructor
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;

    @GetMapping("/send/{number}")
    public ResponseEntity<?> callExternalOtpApi(@PathVariable String number) {
        String otp = generate6DigitOtp();
        String response = otpService.callOtpApi(number, otp);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public String test(){
        return "test";
    }

}

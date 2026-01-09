package com.yash.controller;

import com.yash.dto.MobileNotificationRequest;
import com.yash.entity.User;
import com.yash.repository.UserRepository;
import com.yash.service.NotificationCacheService;
import com.yash.service.OtpService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.yash.util.OtpGenerator.generate6DigitOtp;

@RestController
@AllArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationCacheService cacheService;

    private final OtpService otpService;

    private final UserRepository userRepository;


    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendOtpNotification(@RequestBody MobileNotificationRequest request) {

        // Redis key (unique)
        String redisKey = "MOBILE_NOTIFICATION:" + request.getMobileNumber();
        String otp = generate6DigitOtp();

        // Save notification in Redis with TTL
//        cacheService.saveNotification(redisKey, otp);

        String response = otpService.callOtpApi(request.getMobileNumber(), otp);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getNotification(
            @RequestParam String responseMobileNumber,
            @RequestParam String responseOtp) {

        String redisKey = "MOBILE_NOTIFICATION:" + responseMobileNumber;
        String notification = cacheService.getNotification(redisKey);

        if (notification == null) {
            System.out.println("expired otp : " + responseOtp);
            return ResponseEntity.status(HttpStatus.GONE)
                    .body("OTP expired or not found");
        }

        if (!notification.equals(responseOtp)) {
            System.out.println("wrong otp : " + responseOtp);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("OTP is wrong");
        }

        User user = userRepository.findUserByMobileNumber(responseMobileNumber);
        user.getSellerProfile().setMobileVerified(true);
        userRepository.save(user);



        return ResponseEntity.ok("OTP verified successfully");
    }

}

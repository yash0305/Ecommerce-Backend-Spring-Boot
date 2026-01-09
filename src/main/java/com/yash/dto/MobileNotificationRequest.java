package com.yash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileNotificationRequest {

    private String mobileNumber;
    private String title;
    private String message;
}

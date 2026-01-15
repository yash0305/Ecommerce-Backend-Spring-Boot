package com.yash.dto;

import com.yash.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SellerInfoDTO {

    private Long id;
    private String username;
    private String mobileNumber;
    private String email;
    private ApplicationStatus status;
    private boolean emailVerified;
    private boolean mobileVerified;

}

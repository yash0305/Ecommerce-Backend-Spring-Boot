package com.yash.service;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class OtpService {

    public String callOtpApi(String number, String otp) {


        // Build URL safely
        String url = UriComponentsBuilder
                .fromHttpUrl("https://ciacloud.in/otpapi.php?number=" + number + "&otp=" + otp)
                .queryParam("number", number)
                .queryParam("otp", otp)
                .toUriString();

        // Set Authorization header (same as cURL)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic dGVjaHA6VGVjaFBAIUAj");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                String.class
        );

        System.out.println(response.getBody());
        return response.getBody();

    }
}

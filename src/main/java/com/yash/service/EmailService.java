package com.yash.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body, String verifyLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("ecommerce03051999@gmail.com");

            String htmlContent = """
                        <div style="font-family: Arial, sans-serif; line-height: 1.6;">
                            <h2>Verify Your Seller Account</h2>
                            <p>Thank you for registering as a seller.</p>
                            <p>Please click the button below to verify your email address:</p>
                    
                            <a href="%s"
                               style="
                                   display: inline-block;
                                   padding: 12px 24px;
                                   background-color: #4CAF50;
                                   color: white;
                                   text-decoration: none;
                                   border-radius: 5px;
                                   font-weight: bold;
                               ">
                                Verify Email
                            </a>
                    
                            <p style="margin-top: 20px;">
                                If you did not request this, please ignore this email.
                            </p>
                        </div>
                    """.formatted(verifyLink);

            helper.setText(htmlContent, true); // true = HTML


            mailSender.send(message);
        }
            catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
}
}

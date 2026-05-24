package com.vsg.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.frontend-url:https://www.visionarysalvagroup.com}")
    private String frontendUrl;

    public void sendLoginOtp(String toEmail, String otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toEmail);
        msg.setSubject("Your VSG Login OTP");
        msg.setText("""
                Your one-time login code is:

                  %s

                This code expires in 10 minutes. Do not share it with anyone.
                """.formatted(otp));
        try {
            mailSender.send(msg);
            log.info("Login OTP sent to {}", toEmail);
        } catch (MailException e) {
            log.error("Failed to send login OTP to {}: {}", toEmail, e.getMessage());
            throw e;
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(toEmail);
        msg.setSubject("VSG – Password Reset Request");
        msg.setText("""
                We received a request to reset your password.

                Click the link below to set a new password (valid for 1 hour):

                  %s

                If you did not request this, please ignore this email.
                """.formatted(resetLink));
        try {
            mailSender.send(msg);
            log.info("Password reset email sent to {}", toEmail);
        } catch (MailException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw e;
        }
    }
}

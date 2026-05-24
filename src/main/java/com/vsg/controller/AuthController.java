package com.vsg.controller;

import com.vsg.dto.*;
import com.vsg.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Legacy direct login — email + password → JWT (no OTP) */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    /** OTP login step 1 — validate credentials → send OTP to email */
    @PostMapping("/login/init")
    public ResponseEntity<MessageResponse> loginInit(@Valid @RequestBody OtpInitRequest req) {
        return ResponseEntity.ok(authService.loginInit(req));
    }

    /** OTP login step 2 — submit OTP → receive JWT */
    @PostMapping("/login/verify")
    public ResponseEntity<LoginResponse> loginVerify(@Valid @RequestBody OtpVerifyRequest req) {
        return ResponseEntity.ok(authService.loginVerify(req));
    }

    /** Request a password reset email */
    @PostMapping("/password/forgot")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        return ResponseEntity.ok(authService.forgotPassword(req));
    }

    /** Submit reset token + new password */
    @PostMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        return ResponseEntity.ok(authService.resetPassword(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse.AuthUserDto> me(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(authService.me(principal.getUsername()));
    }
}

package com.vsg.service;

import com.vsg.dto.*;
import com.vsg.dto.LoginResponse.AuthUserDto;
import com.vsg.entity.AppUser;
import com.vsg.entity.OtpToken;
import com.vsg.repository.OtpTokenRepository;
import com.vsg.repository.UserRepository;
import com.vsg.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int    OTP_EXPIRY_MINUTES  = 10;
    private static final int    RESET_EXPIRY_MINUTES = 60;
    private static final String TYPE_LOGIN_OTP       = "LOGIN_OTP";
    private static final String TYPE_PASSWORD_RESET  = "PASSWORD_RESET";

    private final UserRepository     userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder    passwordEncoder;
    private final JwtUtil            jwtUtil;
    private final EmailService       emailService;

    // ─── Legacy direct login (kept for backward compat) ──────────────────────
    public LoginResponse login(LoginRequest req) {
        AppUser user = findActiveUser(req.getEmail());
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return buildLoginResponse(user);
    }

    // ─── Step 1: validate credentials → send OTP ─────────────────────────────
    @Transactional
    public MessageResponse loginInit(OtpInitRequest req) {
        AppUser user = findActiveUser(req.email());
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        String otp = generateOtp();
        saveToken(user.getEmail(), user.getTenantId(),
                passwordEncoder.encode(otp), TYPE_LOGIN_OTP, OTP_EXPIRY_MINUTES);
        emailService.sendLoginOtp(user.getEmail(), otp);
        return new MessageResponse("OTP sent to " + maskEmail(user.getEmail()));
    }

    // ─── Step 2: verify OTP → return JWT ─────────────────────────────────────
    @Transactional
    public LoginResponse loginVerify(OtpVerifyRequest req) {
        OtpToken record = otpTokenRepository
                .findFirstByEmailAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        req.email().toLowerCase(), TYPE_LOGIN_OTP, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "OTP expired or not found"));

        if (!passwordEncoder.matches(req.otp(), record.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP");
        }
        record.setUsed(true);
        otpTokenRepository.save(record);
        return buildLoginResponse(findActiveUser(req.email()));
    }

    // ─── Password reset: send reset link ─────────────────────────────────────
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest req) {
        userRepository.findByEmail(req.email().toLowerCase()).ifPresent(user -> {
            String rawToken  = UUID.randomUUID().toString();
            String tokenHash = sha256(rawToken);          // SHA-256: fast + queryable
            saveToken(user.getEmail(), user.getTenantId(),
                    tokenHash, TYPE_PASSWORD_RESET, RESET_EXPIRY_MINUTES);
            emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
        });
        return new MessageResponse("If that email is registered, a reset link has been sent.");
    }

    // ─── Password reset: set new password ────────────────────────────────────
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest req) {
        // Hash the incoming token and do a direct DB lookup — no full table scan
        String tokenHash = sha256(req.token());
        OtpToken record = otpTokenRepository
                .findFirstByTokenAndTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        tokenHash, TYPE_PASSWORD_RESET, LocalDateTime.now())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Token invalid or expired"));

        record.setUsed(true);
        otpTokenRepository.save(record);

        AppUser user = userRepository.findByEmail(record.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
        return new MessageResponse("Password updated successfully.");
    }

    // ─── /auth/me ─────────────────────────────────────────────────────────────
    public AuthUserDto me(String email) {
        return toAuthUserDto(findActiveUser(email));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private AppUser findActiveUser(String email) {
        AppUser user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
        }
        return user;
    }

    @Transactional
    protected void saveToken(String email, String tenantId, String hashedToken,
                             String type, int expiryMinutes) {
        otpTokenRepository.deleteByEmailAndType(email.toLowerCase(), type);
        OtpToken t = new OtpToken();
        t.setEmail(email.toLowerCase());
        t.setTenantId(tenantId);
        t.setToken(hashedToken);
        t.setType(type);
        t.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        otpTokenRepository.save(t);
    }

    private static String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    private static String sha256(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return email;
        return email.charAt(0) + "***" + email.substring(at);
    }

    private LoginResponse buildLoginResponse(AppUser user) {
        String token = jwtUtil.generate(
                user.getId().toString(), user.getEmail(), user.getTenantId(), user.getRoles());
        return new LoginResponse(token, toAuthUserDto(user));
    }

    private AuthUserDto toAuthUserDto(AppUser user) {
        return new AuthUserDto(
                user.getId().toString(),
                user.getEmail(),
                user.getName(),
                Arrays.asList(user.getRoles()),
                user.getPermissions() != null ? Arrays.asList(user.getPermissions()) : null,
                user.getTenantId());
    }
}

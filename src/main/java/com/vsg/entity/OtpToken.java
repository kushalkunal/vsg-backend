package com.vsg.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(name = "tenant_id", length = 50)
    private String tenantId;

    /** Hashed token/OTP value */
    @Column(nullable = false)
    private String token;

    /** LOGIN_OTP or PASSWORD_RESET */
    @Column(nullable = false, length = 30)
    private String type;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    private boolean used = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

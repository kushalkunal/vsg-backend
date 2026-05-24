package com.vsg.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String name;

    @Column(columnDefinition = "TEXT[]")
    private String[] roles = {"COUNSELLOR"};

    @Column(columnDefinition = "TEXT[]")
    private String[] permissions;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean isActive = true;
}

package com.vsg.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "followups")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Followup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String note;

    @Column(name = "reminder_date")
    private LocalDate reminderDate;

    @Column(length = 50)
    private String channel;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

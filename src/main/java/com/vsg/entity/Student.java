package com.vsg.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, length = 50)
    private String phone;

    private String email;

    @Column(name = "interested_course")
    private String interestedCourse;

    @Column(name = "preferred_country", length = 100)
    private String preferredCountry;

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Column(name = "neet_score")
    private Integer neetScore;

    @Column(length = 100)
    private String status = "New Lead";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "assigned_counsellor")
    private String assignedCounsellor;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

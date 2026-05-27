package com.vsg.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "colleges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    private Integer ranking;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "nmc_approved")
    private Boolean nmcApproved = false;

    @Column(name = "who_approved")
    private Boolean whoApproved = false;

    @Column(name = "hostel_available")
    private Boolean hostelAvailable = false;

    @Column(name = "ugc_approved")
    private Boolean ugcApproved = false;

    @Column(name = "aicte_approved")
    private Boolean aicteApproved = false;

    @Column(name = "naac_accredited")
    private Boolean naacAccredited = false;

    @Column(name = "brochure_url")
    private String brochureUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(length = 100)
    private String state;

    @Column(length = 255)
    private String affiliation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

package com.vsg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tenants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Tenant {

    @Id
    @Column(length = 50)
    private String id;

    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "primary_color", length = 20)
    private String primaryColor;

    @Column(length = 10)
    private String currency = "USD";

    @Column(columnDefinition = "TEXT[]")
    private String[] countries;

    @Column(name = "student_statuses", columnDefinition = "TEXT[]")
    private String[] studentStatuses;

    @Column(name = "fee_categories", columnDefinition = "TEXT[]")
    private String[] feeCategories;

    @Column(name = "dashboard_widgets", columnDefinition = "TEXT[]")
    private String[] dashboardWidgets;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

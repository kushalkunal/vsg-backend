package com.vsg.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "college_id")
    private UUID collegeId;

    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "tuition_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal tuitionFee = BigDecimal.ZERO;

    @Column(name = "hostel_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal hostelFee = BigDecimal.ZERO;

    @Column(name = "visa_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal visaFee = BigDecimal.ZERO;

    @Column(name = "insurance_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuranceFee = BigDecimal.ZERO;

    @Column(name = "miscellaneous_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal miscellaneousFee = BigDecimal.ZERO;

    @Column(name = "total_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalFee = BigDecimal.ZERO;

    @Column(length = 10)
    private String currency = "USD";

    @Column(length = 100)
    private String branch;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    protected void computeTotal() {
        this.totalFee = tuitionFee.add(hostelFee).add(visaFee).add(insuranceFee).add(miscellaneousFee);
    }
}

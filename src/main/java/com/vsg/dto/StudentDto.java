package com.vsg.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StudentDto {
    private String id;
    private String fullName;
    private String phone;
    private String email;
    private String interestedCourse;
    private String preferredCountry;
    private BigDecimal budget;
    private Integer neetScore;
    private String status;
    private String notes;
    private String assignedCounsellor;
    private String createdAt;
    private String updatedAt;
}

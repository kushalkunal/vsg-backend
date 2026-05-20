package com.vsg.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeDto {
    private String id;
    private String collegeId;
    private String courseId;
    private String collegeName;
    private String courseName;
    private String branch;
    private BigDecimal tuitionFee;
    private BigDecimal hostelFee;
    private BigDecimal visaFee;
    private BigDecimal insuranceFee;
    private BigDecimal miscellaneousFee;
    private BigDecimal totalFee;
    private String currency;
}

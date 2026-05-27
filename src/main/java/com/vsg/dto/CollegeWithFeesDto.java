package com.vsg.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CollegeWithFeesDto {
    private String id;
    private String name;
    private String city;
    private String state;
    private String country;
    private Integer ranking;
    private String description;
    private Boolean nmcApproved;
    private Boolean whoApproved;
    private Boolean hostelAvailable;
    private Boolean ugcApproved;
    private Boolean aicteApproved;
    private Boolean naacAccredited;
    private String imageUrl;
    private String brochureUrl;
    private String affiliation;
    private List<FeeSummaryDto> fees;

    @Data
    public static class FeeSummaryDto {
        private String id;
        private String courseName;
        private String branch;
        private BigDecimal registrationFee;
        private BigDecimal tuitionFee;
        private BigDecimal examinationFee;
        private BigDecimal hostelFee;
        private BigDecimal totalPkgWithoutHostel;
        private BigDecimal totalPkgWithHostel;
        private BigDecimal miscellaneousFee;
        private BigDecimal totalFee;
        private String currency;
    }
}

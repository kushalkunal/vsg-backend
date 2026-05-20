package com.vsg.dto;

import lombok.Data;

@Data
public class CollegeDto {
    private String id;
    private String name;
    private String country;
    private String city;
    private Integer ranking;
    private String description;
    private Boolean nmcApproved;
    private Boolean whoApproved;
    private Boolean hostelAvailable;
    private String brochureUrl;
    private String imageUrl;
    private String state;
    private String affiliation;
}

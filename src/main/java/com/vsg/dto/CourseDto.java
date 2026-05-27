package com.vsg.dto;

import lombok.Data;

@Data
public class CourseDto {
    private String id;
    private String name;
    private String description;
    private Integer durationYears;
}

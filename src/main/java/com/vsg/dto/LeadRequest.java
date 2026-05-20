package com.vsg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LeadRequest {
    @NotBlank
    private String tenantId;

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String phone;

    @Size(max = 120)
    private String email;

    @Size(max = 100)
    private String course;

    @Size(max = 600)
    private String notes;
}

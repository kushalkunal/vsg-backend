package com.vsg.dto;

import lombok.Data;

import java.util.List;

@Data
public class TenantSettingsDto {
    private String name;
    private String logoUrl;
    private String primaryColor;
    private String currency;
    private List<String> countries;
    private List<String> studentStatuses;
    private List<String> feeCategories;
    private List<String> dashboardWidgets;
}

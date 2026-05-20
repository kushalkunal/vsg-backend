package com.vsg.service;

import com.vsg.dto.TenantSettingsDto;
import com.vsg.entity.Tenant;
import com.vsg.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final TenantRepository repo;

    public TenantSettingsDto get(String tenantId) {
        return toDto(findTenant(tenantId));
    }

    public TenantSettingsDto update(String tenantId, TenantSettingsDto dto) {
        Tenant t = findTenant(tenantId);
        if (dto.getName()             != null) t.setName(dto.getName());
        if (dto.getLogoUrl()          != null) t.setLogoUrl(dto.getLogoUrl());
        if (dto.getPrimaryColor()     != null) t.setPrimaryColor(dto.getPrimaryColor());
        if (dto.getCurrency()         != null) t.setCurrency(dto.getCurrency());
        if (dto.getCountries()        != null) t.setCountries(dto.getCountries().toArray(new String[0]));
        if (dto.getStudentStatuses()  != null) t.setStudentStatuses(dto.getStudentStatuses().toArray(new String[0]));
        if (dto.getFeeCategories()    != null) t.setFeeCategories(dto.getFeeCategories().toArray(new String[0]));
        if (dto.getDashboardWidgets() != null) t.setDashboardWidgets(dto.getDashboardWidgets().toArray(new String[0]));
        return toDto(repo.save(t));
    }

    private Tenant findTenant(String tenantId) {
        return repo.findById(tenantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found: " + tenantId));
    }

    private TenantSettingsDto toDto(Tenant t) {
        TenantSettingsDto d = new TenantSettingsDto();
        d.setName(t.getName());
        d.setLogoUrl(t.getLogoUrl());
        d.setPrimaryColor(t.getPrimaryColor());
        d.setCurrency(t.getCurrency());
        d.setCountries(toList(t.getCountries()));
        d.setStudentStatuses(toList(t.getStudentStatuses()));
        d.setFeeCategories(toList(t.getFeeCategories()));
        d.setDashboardWidgets(toList(t.getDashboardWidgets()));
        return d;
    }

    private List<String> toList(String[] arr) {
        return arr != null ? Arrays.asList(arr) : null;
    }
}

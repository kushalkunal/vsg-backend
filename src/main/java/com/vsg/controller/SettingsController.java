package com.vsg.controller;

import com.vsg.dto.TenantSettingsDto;
import com.vsg.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenantId}/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService service;

    @GetMapping
    public ResponseEntity<TenantSettingsDto> get(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.get(tenantId));
    }

    @PutMapping
    public ResponseEntity<TenantSettingsDto> update(
        @PathVariable String tenantId,
        @RequestBody TenantSettingsDto dto
    ) {
        return ResponseEntity.ok(service.update(tenantId, dto));
    }
}

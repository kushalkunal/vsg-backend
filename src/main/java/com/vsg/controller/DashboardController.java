package com.vsg.controller;

import com.vsg.dto.DashboardStatsDto;
import com.vsg.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenantId}/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> stats(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.stats(tenantId));
    }
}

package com.vsg.controller;

import com.vsg.dto.FeeDto;
import com.vsg.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{tenantId}/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService service;

    @GetMapping
    public ResponseEntity<List<FeeDto>> list(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeDto> get(@PathVariable String tenantId, @PathVariable String id) {
        return ResponseEntity.ok(service.get(tenantId, id));
    }

    @PostMapping
    public ResponseEntity<FeeDto> create(@PathVariable String tenantId, @RequestBody FeeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(tenantId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeeDto> update(
        @PathVariable String tenantId,
        @PathVariable String id,
        @RequestBody FeeDto dto
    ) {
        return ResponseEntity.ok(service.update(tenantId, id, dto));
    }
}

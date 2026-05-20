package com.vsg.controller;

import com.vsg.dto.CollegeDto;
import com.vsg.dto.CollegeWithFeesDto;
import com.vsg.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/{tenantId}/colleges")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService service;

    @GetMapping
    public ResponseEntity<List<CollegeDto>> list(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CollegeDto>> search(
        @PathVariable String tenantId,
        @RequestParam(required = false) String course,
        @RequestParam(required = false) BigDecimal budget,
        @RequestParam(required = false) String country
    ) {
        return ResponseEntity.ok(service.search(tenantId, country, budget));
    }

    @GetMapping("/with-fees")
    public ResponseEntity<List<CollegeWithFeesDto>> searchWithFees(
        @PathVariable String tenantId,
        @RequestParam(required = false) String course,
        @RequestParam(required = false) String branch,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String country
    ) {
        return ResponseEntity.ok(service.searchWithFees(tenantId, course, branch, city, country));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollegeDto> get(
        @PathVariable String tenantId,
        @PathVariable String id
    ) {
        return ResponseEntity.ok(service.get(tenantId, id));
    }

    @PostMapping
    public ResponseEntity<CollegeDto> create(
        @PathVariable String tenantId,
        @RequestBody CollegeDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(tenantId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollegeDto> update(
        @PathVariable String tenantId,
        @PathVariable String id,
        @RequestBody CollegeDto dto
    ) {
        return ResponseEntity.ok(service.update(tenantId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable String tenantId,
        @PathVariable String id
    ) {
        service.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}

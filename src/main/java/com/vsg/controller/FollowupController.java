package com.vsg.controller;

import com.vsg.dto.FollowupDto;
import com.vsg.service.FollowupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{tenantId}/followups")
@RequiredArgsConstructor
public class FollowupController {

    private final FollowupService service;

    @GetMapping
    public ResponseEntity<List<FollowupDto>> list(
        @PathVariable String tenantId,
        @RequestParam(required = false) String studentId
    ) {
        if (studentId != null && !studentId.isBlank()) {
            return ResponseEntity.ok(service.listByStudent(tenantId, studentId));
        }
        return ResponseEntity.ok(service.listAll(tenantId));
    }

    @PostMapping
    public ResponseEntity<FollowupDto> create(
        @PathVariable String tenantId,
        @RequestBody FollowupDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(tenantId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String tenantId, @PathVariable String id) {
        service.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}

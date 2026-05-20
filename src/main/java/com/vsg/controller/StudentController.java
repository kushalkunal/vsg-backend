package com.vsg.controller;

import com.vsg.dto.PageResponse;
import com.vsg.dto.StudentDto;
import com.vsg.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{tenantId}/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @GetMapping
    public ResponseEntity<PageResponse<StudentDto>> list(
        @PathVariable String tenantId,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) String course,
        @RequestParam(defaultValue = "0")  int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(service.list(tenantId, search, status, country, course, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> get(
        @PathVariable String tenantId,
        @PathVariable String id
    ) {
        return ResponseEntity.ok(service.get(tenantId, id));
    }

    @PostMapping
    public ResponseEntity<StudentDto> create(
        @PathVariable String tenantId,
        @RequestBody StudentDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(tenantId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> update(
        @PathVariable String tenantId,
        @PathVariable String id,
        @RequestBody StudentDto dto
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

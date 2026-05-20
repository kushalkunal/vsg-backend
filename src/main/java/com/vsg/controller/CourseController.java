package com.vsg.controller;

import com.vsg.dto.CourseDto;
import com.vsg.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{tenantId}/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    @GetMapping
    public ResponseEntity<List<CourseDto>> list(@PathVariable String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> get(@PathVariable String tenantId, @PathVariable String id) {
        return ResponseEntity.ok(service.get(tenantId, id));
    }

    @PostMapping
    public ResponseEntity<CourseDto> create(@PathVariable String tenantId, @RequestBody CourseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(tenantId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDto> update(
        @PathVariable String tenantId,
        @PathVariable String id,
        @RequestBody CourseDto dto
    ) {
        return ResponseEntity.ok(service.update(tenantId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String tenantId, @PathVariable String id) {
        service.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }
}

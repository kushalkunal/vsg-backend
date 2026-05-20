package com.vsg.controller;

import com.vsg.dto.LeadRequest;
import com.vsg.dto.StudentDto;
import com.vsg.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public endpoint — no JWT required.
 * Used by the website inquiry / free-admission forms to capture leads.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicLeadController {

    private final StudentService studentService;

    @PostMapping("/leads")
    public ResponseEntity<StudentDto> submitLead(@Valid @RequestBody LeadRequest req) {
        StudentDto dto = new StudentDto();
        dto.setFullName(req.getName());
        dto.setPhone(req.getPhone());
        dto.setEmail(req.getEmail());
        dto.setInterestedCourse(req.getCourse());
        dto.setNotes(req.getNotes());
        dto.setStatus("NEW");

        StudentDto created = studentService.create(req.getTenantId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

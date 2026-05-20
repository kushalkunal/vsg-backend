package com.vsg.service;

import com.vsg.dto.StudentDto;
import com.vsg.dto.PageResponse;
import com.vsg.entity.Student;
import com.vsg.repository.StudentRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repo;

    public PageResponse<StudentDto> list(
        String tenantId, String search, String status,
        String country, String course, int page, int size
    ) {
        String searchVal  = blankToNull(search);
        String statusVal  = blankToNull(status);
        String countryVal = blankToNull(country);
        String courseVal  = blankToNull(course);

        Specification<Student> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            if (searchVal != null) {
                String pattern = "%" + searchVal.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
                ));
            }
            if (statusVal != null) {
                predicates.add(cb.equal(root.get("status"), statusVal));
            }
            if (countryVal != null) {
                predicates.add(cb.equal(root.get("preferredCountry"), countryVal));
            }
            if (courseVal != null) {
                String pattern = "%" + courseVal.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("interestedCourse")), pattern));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        var pg = repo.findAll(spec, pageable);
        return PageResponse.of(pg.map(this::toDto));
    }

    public StudentDto get(String tenantId, String id) {
        return toDto(find(tenantId, id));
    }

    public StudentDto create(String tenantId, StudentDto dto) {
        Student s = new Student();
        s.setTenantId(tenantId);
        apply(s, dto);
        return toDto(repo.save(s));
    }

    public StudentDto update(String tenantId, String id, StudentDto dto) {
        Student s = find(tenantId, id);
        apply(s, dto);
        return toDto(repo.save(s));
    }

    public void delete(String tenantId, String id) {
        Student s = find(tenantId, id);
        repo.delete(s);
    }

    // ---- helpers ----

    private Student find(String tenantId, String id) {
        return repo.findByIdAndTenantId(UUID.fromString(id), tenantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    private void apply(Student s, StudentDto dto) {
        if (dto.getFullName()           != null) s.setFullName(dto.getFullName());
        if (dto.getPhone()              != null) s.setPhone(dto.getPhone());
        if (dto.getEmail()              != null) s.setEmail(dto.getEmail());
        if (dto.getInterestedCourse()   != null) s.setInterestedCourse(dto.getInterestedCourse());
        if (dto.getPreferredCountry()   != null) s.setPreferredCountry(dto.getPreferredCountry());
        if (dto.getBudget()             != null) s.setBudget(dto.getBudget());
        if (dto.getNeetScore()          != null) s.setNeetScore(dto.getNeetScore());
        if (dto.getStatus()             != null) s.setStatus(dto.getStatus());
        if (dto.getNotes()              != null) s.setNotes(dto.getNotes());
        if (dto.getAssignedCounsellor() != null) s.setAssignedCounsellor(dto.getAssignedCounsellor());
    }

    private StudentDto toDto(Student s) {
        StudentDto d = new StudentDto();
        d.setId(s.getId().toString());
        d.setFullName(s.getFullName());
        d.setPhone(s.getPhone());
        d.setEmail(s.getEmail());
        d.setInterestedCourse(s.getInterestedCourse());
        d.setPreferredCountry(s.getPreferredCountry());
        d.setBudget(s.getBudget());
        d.setNeetScore(s.getNeetScore());
        d.setStatus(s.getStatus());
        d.setNotes(s.getNotes());
        d.setAssignedCounsellor(s.getAssignedCounsellor());
        d.setCreatedAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null);
        d.setUpdatedAt(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : null);
        return d;
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}

package com.vsg.service;

import com.vsg.dto.CourseDto;
import com.vsg.entity.Course;
import com.vsg.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository repo;

    public List<CourseDto> list(String tenantId) {
        return repo.findByTenantIdOrderByNameAsc(tenantId).stream().map(this::toDto).toList();
    }

    public CourseDto get(String tenantId, String id) {
        return toDto(find(tenantId, id));
    }

    public CourseDto create(String tenantId, CourseDto dto) {
        Course c = new Course();
        c.setTenantId(tenantId);
        apply(c, dto);
        return toDto(repo.save(c));
    }

    public CourseDto update(String tenantId, String id, CourseDto dto) {
        Course c = find(tenantId, id);
        apply(c, dto);
        return toDto(repo.save(c));
    }

    public void delete(String tenantId, String id) {
        repo.delete(find(tenantId, id));
    }

    private Course find(String tenantId, String id) {
        return repo.findByIdAndTenantId(UUID.fromString(id), tenantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    private void apply(Course c, CourseDto dto) {
        if (dto.getName()           != null) c.setName(dto.getName());
        if (dto.getDescription()    != null) c.setDescription(dto.getDescription());
        if (dto.getDurationYears()   != null) c.setDurationYears(dto.getDurationYears());
    }

    private CourseDto toDto(Course c) {
        CourseDto d = new CourseDto();
        d.setId(c.getId().toString());
        d.setName(c.getName());
        d.setDescription(c.getDescription());
        d.setDurationYears(c.getDurationYears());
        return d;
    }
}

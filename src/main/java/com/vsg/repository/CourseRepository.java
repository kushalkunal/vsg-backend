package com.vsg.repository;

import com.vsg.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByTenantIdOrderByNameAsc(String tenantId);
    Optional<Course> findByIdAndTenantId(UUID id, String tenantId);
    List<Course> findByTenantIdAndNameIgnoreCase(String tenantId, String name);
}

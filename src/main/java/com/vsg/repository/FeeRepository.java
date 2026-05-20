package com.vsg.repository;

import com.vsg.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeRepository extends JpaRepository<Fee, UUID> {
    List<Fee> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    Optional<Fee> findByIdAndTenantId(UUID id, String tenantId);
    List<Fee> findByTenantIdAndCourseIdIn(String tenantId, List<UUID> courseIds);
}

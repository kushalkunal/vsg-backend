package com.vsg.repository;

import com.vsg.entity.Followup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowupRepository extends JpaRepository<Followup, UUID> {
    List<Followup> findByTenantIdAndStudentIdOrderByCreatedAtDesc(String tenantId, UUID studentId);
    List<Followup> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    Optional<Followup> findByIdAndTenantId(UUID id, String tenantId);
}

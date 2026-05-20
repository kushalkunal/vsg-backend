package com.vsg.repository;

import com.vsg.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollegeRepository extends JpaRepository<College, UUID>, JpaSpecificationExecutor<College> {

    List<College> findByTenantIdOrderByNameAsc(String tenantId);

    Optional<College> findByIdAndTenantId(UUID id, String tenantId);

    List<College> findByTenantIdAndIdInOrderByNameAsc(String tenantId, List<UUID> ids);
}

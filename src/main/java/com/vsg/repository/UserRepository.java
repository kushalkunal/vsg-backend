package com.vsg.repository;

import com.vsg.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findByTenantIdOrderByCreatedAtAsc(String tenantId);
    long countByTenantId(String tenantId);
    boolean existsByEmailAndTenantId(String email, String tenantId);
}

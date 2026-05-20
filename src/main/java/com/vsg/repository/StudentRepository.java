package com.vsg.repository;

import com.vsg.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {

    Optional<Student> findByIdAndTenantId(UUID id, String tenantId);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, String status);

    @Query("""
        SELECT s.preferredCountry AS name, COUNT(s) AS count
        FROM Student s
        WHERE s.tenantId = :tenantId AND s.preferredCountry IS NOT NULL
        GROUP BY s.preferredCountry
        ORDER BY COUNT(s) DESC
        LIMIT 5
        """)
    java.util.List<Object[]> topCountries(@Param("tenantId") String tenantId);

    @Query("""
        SELECT s.interestedCourse AS name, COUNT(s) AS count
        FROM Student s
        WHERE s.tenantId = :tenantId AND s.interestedCourse IS NOT NULL
        GROUP BY s.interestedCourse
        ORDER BY COUNT(s) DESC
        LIMIT 5
        """)
    java.util.List<Object[]> topCourses(@Param("tenantId") String tenantId);

    @Query("""
        SELECT s FROM Student s
        WHERE s.tenantId = :tenantId
        ORDER BY s.createdAt DESC
        LIMIT 5
        """)
    java.util.List<Student> recentEnquiries(@Param("tenantId") String tenantId);
}

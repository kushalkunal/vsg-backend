package com.vsg.service;

import com.vsg.dto.DashboardStatsDto;
import com.vsg.dto.DashboardStatsDto.*;
import com.vsg.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepo;

    public DashboardStatsDto stats(String tenantId) {
        long totalStudents       = studentRepo.countByTenantId(tenantId);
        long activeLeads         = studentRepo.countByTenantIdAndStatus(tenantId, "New Lead")
                                 + studentRepo.countByTenantIdAndStatus(tenantId, "Contacted");
        long admissionsConfirmed = studentRepo.countByTenantIdAndStatus(tenantId, "Enrolled");

        List<NameCount> topCountries = studentRepo.topCountries(tenantId).stream()
            .map(row -> new NameCount((String) row[0], ((Number) row[1]).longValue()))
            .toList();

        List<NameCount> topCourses = studentRepo.topCourses(tenantId).stream()
            .map(row -> new NameCount((String) row[0], ((Number) row[1]).longValue()))
            .toList();

        List<RecentEnquiry> recentEnquiries = studentRepo.recentEnquiries(tenantId).stream()
            .map(s -> new RecentEnquiry(
                s.getId().toString(),
                s.getFullName(),
                s.getCreatedAt() != null ? s.getCreatedAt().toString() : null
            ))
            .toList();

        return new DashboardStatsDto(
            totalStudents, activeLeads, admissionsConfirmed,
            topCountries, topCourses, recentEnquiries
        );
    }
}

package com.vsg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class DashboardStatsDto {
    private long totalStudents;
    private long activeLeads;
    private long admissionsConfirmed;
    private List<NameCount> topCountries;
    private List<NameCount> topCourses;
    private List<RecentEnquiry> recentEnquiries;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class NameCount {
        private String name;
        private long count;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RecentEnquiry {
        private String id;
        private String name;
        private String createdAt;
    }
}

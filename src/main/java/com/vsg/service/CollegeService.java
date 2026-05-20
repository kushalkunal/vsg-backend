package com.vsg.service;

import com.vsg.dto.CollegeDto;
import com.vsg.dto.CollegeWithFeesDto;
import com.vsg.entity.College;
import com.vsg.entity.Course;
import com.vsg.entity.Fee;
import com.vsg.repository.CollegeRepository;
import com.vsg.repository.CourseRepository;
import com.vsg.repository.FeeRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollegeService {

    private final CollegeRepository repo;
    private final CourseRepository courseRepo;
    private final FeeRepository feeRepo;

    public List<CollegeDto> list(String tenantId) {
        return repo.findByTenantIdOrderByNameAsc(tenantId).stream().map(this::toDto).toList();
    }

    public CollegeDto get(String tenantId, String id) {
        return toDto(find(tenantId, id));
    }

    public CollegeDto create(String tenantId, CollegeDto dto) {
        College c = new College();
        c.setTenantId(tenantId);
        apply(c, dto);
        return toDto(repo.save(c));
    }

    public CollegeDto update(String tenantId, String id, CollegeDto dto) {
        College c = find(tenantId, id);
        apply(c, dto);
        return toDto(repo.save(c));
    }

    public void delete(String tenantId, String id) {
        repo.delete(find(tenantId, id));
    }

    public List<CollegeDto> search(String tenantId, String country, BigDecimal budget) {
        Specification<College> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            if (country != null && !country.isBlank()) {
                predicates.add(cb.equal(root.get("country"), country));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repo.findAll(spec).stream()
            .sorted(java.util.Comparator.comparing(College::getName))
            .map(this::toDto)
            .toList();
    }

    private College find(String tenantId, String id) {
        return repo.findByIdAndTenantId(UUID.fromString(id), tenantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "College not found"));
    }

    private void apply(College c, CollegeDto dto) {
        if (dto.getName()            != null) c.setName(dto.getName());
        if (dto.getCountry()         != null) c.setCountry(dto.getCountry());
        if (dto.getCity()            != null) c.setCity(dto.getCity());
        if (dto.getRanking()         != null) c.setRanking(dto.getRanking());
        if (dto.getDescription()     != null) c.setDescription(dto.getDescription());
        if (dto.getNmcApproved()     != null) c.setNmcApproved(dto.getNmcApproved());
        if (dto.getWhoApproved()     != null) c.setWhoApproved(dto.getWhoApproved());
        if (dto.getHostelAvailable() != null) c.setHostelAvailable(dto.getHostelAvailable());
        if (dto.getBrochureUrl()     != null) c.setBrochureUrl(dto.getBrochureUrl());
        if (dto.getImageUrl()        != null) c.setImageUrl(dto.getImageUrl());
        if (dto.getState()           != null) c.setState(dto.getState());
        if (dto.getAffiliation()     != null) c.setAffiliation(dto.getAffiliation());
    }

    private CollegeDto toDto(College c) {
        CollegeDto d = new CollegeDto();
        d.setId(c.getId().toString());
        d.setName(c.getName());
        d.setCountry(c.getCountry());
        d.setCity(c.getCity());
        d.setRanking(c.getRanking());
        d.setDescription(c.getDescription());
        d.setNmcApproved(c.getNmcApproved());
        d.setWhoApproved(c.getWhoApproved());
        d.setHostelAvailable(c.getHostelAvailable());
        d.setBrochureUrl(c.getBrochureUrl());
        d.setImageUrl(c.getImageUrl());
        d.setState(c.getState());
        d.setAffiliation(c.getAffiliation());
        return d;
    }

    /**
     * Search colleges that offer a given course (and optional branch) in the specified city/country.
     * Returns each matching college with its per-branch fee breakdown.
     */
    public List<CollegeWithFeesDto> searchWithFees(
            String tenantId, String courseName, String branch, String city, String country) {

        // 1. Find courses matching the requested name
        List<Course> courses = (courseName != null && !courseName.isBlank())
                ? courseRepo.findByTenantIdAndNameIgnoreCase(tenantId, courseName)
                : courseRepo.findByTenantIdOrderByNameAsc(tenantId);

        if (courses.isEmpty()) return List.of();

        Map<UUID, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));
        List<UUID> courseIds = new ArrayList<>(courseMap.keySet());

        // 2. Fetch fees for those courses
        List<Fee> fees = feeRepo.findByTenantIdAndCourseIdIn(tenantId, courseIds);

        // 3. Filter by branch if specified
        if (branch != null && !branch.isBlank()) {
            final String branchLower = branch.trim().toLowerCase();
            fees = fees.stream()
                    .filter(f -> f.getBranch() != null && f.getBranch().toLowerCase().equals(branchLower))
                    .toList();
        }

        if (fees.isEmpty()) return List.of();

        // 4. Group fees by collegeId
        Map<UUID, List<Fee>> feesByCollege = fees.stream()
                .filter(f -> f.getCollegeId() != null)
                .collect(Collectors.groupingBy(Fee::getCollegeId));

        if (feesByCollege.isEmpty()) return List.of();

        // 5. Fetch colleges by IDs
        List<College> colleges = repo.findByTenantIdAndIdInOrderByNameAsc(
                tenantId, new ArrayList<>(feesByCollege.keySet()));

        // 6. Apply city / country filter
        final String cityLower    = (city    != null && !city.isBlank())    ? city.trim().toLowerCase()    : null;
        final String countryLower = (country != null && !country.isBlank()) ? country.trim().toLowerCase() : null;

        return colleges.stream()
                .filter(c -> cityLower    == null || (c.getCity()    != null && c.getCity().toLowerCase().contains(cityLower)))
                .filter(c -> countryLower == null || c.getCountry().toLowerCase().equals(countryLower))
                .map(c -> toCollegeWithFeesDto(c, feesByCollege.get(c.getId()), courseMap))
                .toList();
    }

    private CollegeWithFeesDto toCollegeWithFeesDto(
            College c, List<Fee> fees, Map<UUID, Course> courseMap) {
        CollegeWithFeesDto dto = new CollegeWithFeesDto();
        dto.setId(c.getId().toString());
        dto.setName(c.getName());
        dto.setCity(c.getCity());
        dto.setState(c.getState());
        dto.setCountry(c.getCountry());
        dto.setRanking(c.getRanking());
        dto.setDescription(c.getDescription());
        dto.setNmcApproved(c.getNmcApproved());
        dto.setWhoApproved(c.getWhoApproved());
        dto.setHostelAvailable(c.getHostelAvailable());
        dto.setImageUrl(c.getImageUrl());
        dto.setBrochureUrl(c.getBrochureUrl());
        dto.setAffiliation(c.getAffiliation());

        List<CollegeWithFeesDto.FeeSummaryDto> summaries = fees == null ? List.of()
                : fees.stream().map(f -> {
                    CollegeWithFeesDto.FeeSummaryDto s = new CollegeWithFeesDto.FeeSummaryDto();
                    s.setId(f.getId().toString());
                    Course course = f.getCourseId() != null ? courseMap.get(f.getCourseId()) : null;
                    s.setCourseName(course != null ? course.getName() : null);
                    s.setBranch(f.getBranch());
                    s.setTuitionFee(f.getTuitionFee());
                    s.setHostelFee(f.getHostelFee());
                    s.setVisaFee(f.getVisaFee());
                    s.setInsuranceFee(f.getInsuranceFee());
                    s.setMiscellaneousFee(f.getMiscellaneousFee());
                    s.setTotalFee(f.getTotalFee());
                    s.setCurrency(f.getCurrency());
                    return s;
                }).toList();

        dto.setFees(summaries);
        return dto;
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}

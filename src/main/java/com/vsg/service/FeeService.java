package com.vsg.service;

import com.vsg.dto.FeeDto;
import com.vsg.entity.Fee;
import com.vsg.repository.FeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeRepository repo;

    public List<FeeDto> list(String tenantId) {
        return repo.findByTenantIdOrderByCreatedAtDesc(tenantId).stream().map(this::toDto).toList();
    }

    public FeeDto get(String tenantId, String id) {
        return toDto(find(tenantId, id));
    }

    public FeeDto create(String tenantId, FeeDto dto) {
        Fee f = new Fee();
        f.setTenantId(tenantId);
        apply(f, dto);
        return toDto(repo.save(f));
    }

    public FeeDto update(String tenantId, String id, FeeDto dto) {
        Fee f = find(tenantId, id);
        apply(f, dto);
        return toDto(repo.save(f));
    }

    public void delete(String tenantId, String id) {
        Fee f = find(tenantId, id);
        repo.delete(f);
    }

    private Fee find(String tenantId, String id) {
        return repo.findByIdAndTenantId(UUID.fromString(id), tenantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee record not found"));
    }

    private void apply(Fee f, FeeDto dto) {
        if (dto.getCollegeId()        != null) f.setCollegeId(UUID.fromString(dto.getCollegeId()));
        if (dto.getCourseId()         != null) f.setCourseId(UUID.fromString(dto.getCourseId()));
        if (dto.getBranch()           != null) f.setBranch(dto.getBranch());
        if (dto.getTuitionFee()       != null) f.setTuitionFee(dto.getTuitionFee());
        if (dto.getHostelFee()        != null) f.setHostelFee(dto.getHostelFee());
        if (dto.getVisaFee()          != null) f.setVisaFee(dto.getVisaFee());
        if (dto.getInsuranceFee()     != null) f.setInsuranceFee(dto.getInsuranceFee());
        if (dto.getMiscellaneousFee() != null) f.setMiscellaneousFee(dto.getMiscellaneousFee());
        if (dto.getCurrency()         != null) f.setCurrency(dto.getCurrency());
        // totalFee is auto-computed in @PrePersist/@PreUpdate
    }

    private FeeDto toDto(Fee f) {
        FeeDto d = new FeeDto();
        d.setId(f.getId().toString());
        d.setCollegeId(f.getCollegeId() != null ? f.getCollegeId().toString() : null);
        d.setCourseId(f.getCourseId()   != null ? f.getCourseId().toString()  : null);
        d.setBranch(f.getBranch());
        d.setTuitionFee(f.getTuitionFee());
        d.setHostelFee(f.getHostelFee());
        d.setVisaFee(f.getVisaFee());
        d.setInsuranceFee(f.getInsuranceFee());
        d.setMiscellaneousFee(f.getMiscellaneousFee());
        d.setTotalFee(f.getTotalFee());
        d.setCurrency(f.getCurrency());
        return d;
    }
}

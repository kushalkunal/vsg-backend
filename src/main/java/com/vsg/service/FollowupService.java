package com.vsg.service;

import com.vsg.dto.FollowupDto;
import com.vsg.entity.Followup;
import com.vsg.repository.FollowupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowupService {

    private final FollowupRepository repo;

    public List<FollowupDto> listByStudent(String tenantId, String studentId) {
        return repo.findByTenantIdAndStudentIdOrderByCreatedAtDesc(tenantId, UUID.fromString(studentId))
            .stream().map(this::toDto).toList();
    }

    public List<FollowupDto> listAll(String tenantId) {
        return repo.findByTenantIdOrderByCreatedAtDesc(tenantId)
            .stream().map(this::toDto).toList();
    }

    public FollowupDto create(String tenantId, FollowupDto dto) {
        Followup f = new Followup();
        f.setTenantId(tenantId);
        f.setStudentId(UUID.fromString(dto.getStudentId()));
        f.setNote(dto.getNote());
        if (dto.getReminderDate() != null) f.setReminderDate(LocalDate.parse(dto.getReminderDate()));
        f.setChannel(dto.getChannel());
        f.setCreatedBy(dto.getCreatedBy());
        return toDto(repo.save(f));
    }

    public void delete(String tenantId, String id) {
        Followup f = repo.findByIdAndTenantId(UUID.fromString(id), tenantId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Followup not found"));
        repo.delete(f);
    }

    private FollowupDto toDto(Followup f) {
        FollowupDto d = new FollowupDto();
        d.setId(f.getId().toString());
        d.setStudentId(f.getStudentId().toString());
        d.setNote(f.getNote());
        d.setReminderDate(f.getReminderDate() != null ? f.getReminderDate().toString() : null);
        d.setChannel(f.getChannel());
        d.setCreatedBy(f.getCreatedBy());
        d.setCreatedAt(f.getCreatedAt() != null ? f.getCreatedAt().toString() : null);
        return d;
    }
}

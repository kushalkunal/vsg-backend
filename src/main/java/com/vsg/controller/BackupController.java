package com.vsg.controller;

import com.vsg.service.ExcelBackupService;
import com.vsg.service.ExcelBackupService.RestoreResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/{tenantId}/backup")
@RequiredArgsConstructor
public class BackupController {

    private final ExcelBackupService backupService;

    /**
     * Download a full Excel backup of all tenant data.
     * GET /api/{tenantId}/backup/download
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@PathVariable String tenantId) throws IOException {
        byte[] data = backupService.exportAll(tenantId);
        String filename = "backup-" + tenantId + "-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(data.length)
            .body(data);
    }

    /**
     * Restore data from an uploaded Excel backup.
     * POST /api/{tenantId}/backup/restore
     */
    @PostMapping(value = "/restore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestoreResult> restore(
        @PathVariable String tenantId,
        @RequestPart("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        RestoreResult result = backupService.restoreAll(tenantId, file);
        return ResponseEntity.ok(result);
    }
}

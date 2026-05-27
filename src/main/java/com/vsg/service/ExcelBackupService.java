package com.vsg.service;

import com.vsg.entity.College;
import com.vsg.entity.Course;
import com.vsg.entity.Fee;
import com.vsg.entity.Student;
import com.vsg.repository.CollegeRepository;
import com.vsg.repository.CourseRepository;
import com.vsg.repository.FeeRepository;
import com.vsg.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelBackupService {

    private final StudentRepository  studentRepo;
    private final CollegeRepository  collegeRepo;
    private final CourseRepository   courseRepo;
    private final FeeRepository      feeRepo;

    // ------------------------------------------------------------------ //
    //  EXPORT                                                              //
    // ------------------------------------------------------------------ //

    public byte[] exportAll(String tenantId) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = buildHeaderStyle(wb);

            writeStudents(wb, headerStyle, tenantId);
            writeColleges(wb, headerStyle, tenantId);
            writeCourses (wb, headerStyle, tenantId);
            writeFees    (wb, headerStyle, tenantId);

            wb.write(out);
            return out.toByteArray();
        }
    }

    private void writeStudents(Workbook wb, CellStyle hs, String tenantId) {
        Sheet sheet = wb.createSheet("Students");
        String[] headers = {
            "ID","Full Name","Phone","Email","Interested Course",
            "Preferred Country","Budget","NEET Score","Status",
            "Notes","Assigned Counsellor","Created At"
        };
        writeHeader(sheet, hs, headers);
        int row = 1;
        for (Student s : studentRepo.findAll().stream()
                .filter(st -> tenantId.equals(st.getTenantId())).toList()) {
            Row r = sheet.createRow(row++);
            setCell(r, 0,  s.getId().toString());
            setCell(r, 1,  s.getFullName());
            setCell(r, 2,  s.getPhone());
            setCell(r, 3,  s.getEmail());
            setCell(r, 4,  s.getInterestedCourse());
            setCell(r, 5,  s.getPreferredCountry());
            setCell(r, 6,  s.getBudget() != null ? s.getBudget().toPlainString() : "");
            setCell(r, 7,  s.getNeetScore() != null ? s.getNeetScore().toString() : "");
            setCell(r, 8,  s.getStatus());
            setCell(r, 9,  s.getNotes());
            setCell(r, 10, s.getAssignedCounsellor());
            setCell(r, 11, s.getCreatedAt() != null ? s.getCreatedAt().toString() : "");
        }
        autoSize(sheet, headers.length);
    }

    private void writeColleges(Workbook wb, CellStyle hs, String tenantId) {
        Sheet sheet = wb.createSheet("Colleges");
        String[] headers = {
            "ID","Name","Country","City","State","Affiliation","Ranking",
            "Description","NMC Approved","WHO Approved","Hostel Available",
            "Brochure URL","Image URL"
        };
        writeHeader(sheet, hs, headers);
        int row = 1;
        for (College c : collegeRepo.findByTenantIdOrderByNameAsc(tenantId)) {
            Row r = sheet.createRow(row++);
            setCell(r, 0,  c.getId().toString());
            setCell(r, 1,  c.getName());
            setCell(r, 2,  c.getCountry());
            setCell(r, 3,  c.getCity());
            setCell(r, 4,  c.getState());
            setCell(r, 5,  c.getAffiliation());
            setCell(r, 6,  c.getRanking() != null ? c.getRanking().toString() : "");
            setCell(r, 7,  c.getDescription());
            setCell(r, 8,  Boolean.TRUE.equals(c.getNmcApproved())     ? "YES" : "NO");
            setCell(r, 9,  Boolean.TRUE.equals(c.getWhoApproved())     ? "YES" : "NO");
            setCell(r, 10, Boolean.TRUE.equals(c.getHostelAvailable()) ? "YES" : "NO");
            setCell(r, 11, c.getBrochureUrl());
            setCell(r, 12, c.getImageUrl());
        }
        autoSize(sheet, headers.length);
    }

    private void writeCourses(Workbook wb, CellStyle hs, String tenantId) {
        Sheet sheet = wb.createSheet("Courses");
        String[] headers = {"ID","Name","Description","Duration (Years)"};
        writeHeader(sheet, hs, headers);
        int row = 1;
        for (Course c : courseRepo.findByTenantIdOrderByNameAsc(tenantId)) {
            Row r = sheet.createRow(row++);
            setCell(r, 0, c.getId().toString());
            setCell(r, 1, c.getName());
            setCell(r, 2, c.getDescription());
            setCell(r, 3, c.getDurationYears() != null ? c.getDurationYears().toString() : "");
        }
        autoSize(sheet, headers.length);
    }

    private void writeFees(Workbook wb, CellStyle hs, String tenantId) {
        Sheet sheet = wb.createSheet("Fees");
        String[] headers = {
            "ID","College ID","Course ID","Branch","Currency",
            "Registration Fee","Tuition Fee (Yearly)","Examination Fee",
            "Hostel Fee","Miscellaneous Fee",
            "Total Pkg Without Hostel","Total Pkg With Hostel","Total Fee"
        };
        writeHeader(sheet, hs, headers);
        int row = 1;
        for (Fee f : feeRepo.findByTenantIdOrderByCreatedAtDesc(tenantId)) {
            Row r = sheet.createRow(row++);
            setCell(r, 0,  f.getId().toString());
            setCell(r, 1,  f.getCollegeId() != null ? f.getCollegeId().toString() : "");
            setCell(r, 2,  f.getCourseId()  != null ? f.getCourseId().toString()  : "");
            setCell(r, 3,  f.getBranch());
            setCell(r, 4,  f.getCurrency());
            setCell(r, 5,  f.getRegistrationFee().toPlainString());
            setCell(r, 6,  f.getTuitionFee().toPlainString());
            setCell(r, 7,  f.getExaminationFee().toPlainString());
            setCell(r, 8,  f.getHostelFee().toPlainString());
            setCell(r, 9,  f.getMiscellaneousFee().toPlainString());
            setCell(r, 10, f.getTotalPkgWithoutHostel().toPlainString());
            setCell(r, 11, f.getTotalPkgWithHostel().toPlainString());
            setCell(r, 12, f.getTotalFee().toPlainString());
        }
        autoSize(sheet, headers.length);
    }

    // ------------------------------------------------------------------ //
    //  RESTORE                                                             //
    // ------------------------------------------------------------------ //

    public RestoreResult restoreAll(String tenantId, MultipartFile file) throws IOException {
        RestoreResult result = new RestoreResult();
        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            restoreStudents(wb, tenantId, result);
            restoreColleges(wb, tenantId, result);
            restoreCourses (wb, tenantId, result);
            restoreFees    (wb, tenantId, result);
        }
        return result;
    }

    private void restoreStudents(Workbook wb, String tenantId, RestoreResult result) {
        Sheet sheet = wb.getSheet("Students");
        if (sheet == null) return;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;
            try {
                String id = str(r, 0);
                Student s = id.isBlank()
                    ? new Student()
                    : studentRepo.findById(UUID.fromString(id)).orElse(new Student());
                if (s.getId() == null && !id.isBlank()) s.setId(UUID.fromString(id));
                s.setTenantId(tenantId);
                s.setFullName(str(r, 1));
                s.setPhone(str(r, 2));
                s.setEmail(strOrNull(r, 3));
                s.setInterestedCourse(strOrNull(r, 4));
                s.setPreferredCountry(strOrNull(r, 5));
                String budget = str(r, 6);
                s.setBudget(budget.isBlank() ? null : new BigDecimal(budget));
                String neet = str(r, 7);
                s.setNeetScore(neet.isBlank() ? null : Integer.parseInt(neet));
                s.setStatus(str(r, 8).isBlank() ? "New Lead" : str(r, 8));
                s.setNotes(strOrNull(r, 9));
                s.setAssignedCounsellor(strOrNull(r, 10));
                if (s.getCreatedAt() == null) s.setCreatedAt(LocalDateTime.now());
                s.setUpdatedAt(LocalDateTime.now());
                studentRepo.save(s);
                result.students++;
            } catch (Exception e) {
                result.errors.add("Students row " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    private void restoreColleges(Workbook wb, String tenantId, RestoreResult result) {
        Sheet sheet = wb.getSheet("Colleges");
        if (sheet == null) return;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;
            try {
                String id = str(r, 0);
                College c = id.isBlank()
                    ? new College()
                    : collegeRepo.findById(UUID.fromString(id)).orElse(new College());
                if (c.getId() == null && !id.isBlank()) c.setId(UUID.fromString(id));
                c.setTenantId(tenantId);
                c.setName(str(r, 1));
                c.setCountry(str(r, 2));
                c.setCity(strOrNull(r, 3));
                c.setState(strOrNull(r, 4));
                c.setAffiliation(strOrNull(r, 5));
                String rank = str(r, 6);
                c.setRanking(rank.isBlank() ? null : Integer.parseInt(rank));
                c.setDescription(strOrNull(r, 7));
                c.setNmcApproved("YES".equalsIgnoreCase(str(r, 8)));
                c.setWhoApproved("YES".equalsIgnoreCase(str(r, 9)));
                c.setHostelAvailable("YES".equalsIgnoreCase(str(r, 10)));
                c.setBrochureUrl(strOrNull(r, 11));
                c.setImageUrl(strOrNull(r, 12));
                if (c.getCreatedAt() == null) c.setCreatedAt(LocalDateTime.now());
                collegeRepo.save(c);
                result.colleges++;
            } catch (Exception e) {
                result.errors.add("Colleges row " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    private void restoreCourses(Workbook wb, String tenantId, RestoreResult result) {
        Sheet sheet = wb.getSheet("Courses");
        if (sheet == null) return;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;
            try {
                String id = str(r, 0);
                Course c = id.isBlank()
                    ? new Course()
                    : courseRepo.findById(UUID.fromString(id)).orElse(new Course());
                if (c.getId() == null && !id.isBlank()) c.setId(UUID.fromString(id));
                c.setTenantId(tenantId);
                c.setName(str(r, 1));
                c.setDescription(strOrNull(r, 2));
                String dur = str(r, 3);
                c.setDurationYears(dur.isBlank() ? null : Integer.parseInt(dur));
                if (c.getCreatedAt() == null) c.setCreatedAt(LocalDateTime.now());
                courseRepo.save(c);
                result.courses++;
            } catch (Exception e) {
                result.errors.add("Courses row " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    private void restoreFees(Workbook wb, String tenantId, RestoreResult result) {
        Sheet sheet = wb.getSheet("Fees");
        if (sheet == null) return;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row r = sheet.getRow(i);
            if (r == null) continue;
            try {
                String id = str(r, 0);
                Fee f = id.isBlank()
                    ? new Fee()
                    : feeRepo.findById(UUID.fromString(id)).orElse(new Fee());
                if (f.getId() == null && !id.isBlank()) f.setId(UUID.fromString(id));
                f.setTenantId(tenantId);
                String colId = str(r, 1);
                f.setCollegeId(colId.isBlank() ? null : UUID.fromString(colId));
                String courId = str(r, 2);
                f.setCourseId(courId.isBlank() ? null : UUID.fromString(courId));
                f.setBranch(strOrNull(r, 3));
                f.setCurrency(str(r, 4).isBlank() ? "USD" : str(r, 4));
                f.setRegistrationFee(decimal(r, 5));
                f.setTuitionFee(decimal(r, 6));
                f.setExaminationFee(decimal(r, 7));
                f.setHostelFee(decimal(r, 8));
                f.setMiscellaneousFee(decimal(r, 9));
                // totalPkg fields will be recomputed by @PrePersist/@PreUpdate
                if (f.getCreatedAt() == null) f.setCreatedAt(LocalDateTime.now());
                feeRepo.save(f);
                result.fees++;
            } catch (Exception e) {
                result.errors.add("Fees row " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Helpers                                                             //
    // ------------------------------------------------------------------ //

    private void writeHeader(Sheet sheet, CellStyle hs, String[] headers) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(hs);
        }
    }

    private void setCell(Row row, int col, String value) {
        row.createCell(col).setCellValue(value != null ? value : "");
    }

    private void autoSize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) sheet.autoSizeColumn(i);
    }

    private CellStyle buildHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private String str(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case NUMERIC -> {
                double d = cell.getNumericCellValue();
                yield (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default      -> cell.getStringCellValue().trim();
        };
    }

    private String strOrNull(Row row, int col) {
        String v = str(row, col);
        return v.isBlank() ? null : v;
    }

    private BigDecimal decimal(Row row, int col) {
        String v = str(row, col);
        return v.isBlank() ? BigDecimal.ZERO : new BigDecimal(v);
    }

    // ------------------------------------------------------------------ //
    //  Result DTO                                                          //
    // ------------------------------------------------------------------ //

    public static class RestoreResult {
        public int students = 0;
        public int colleges = 0;
        public int courses  = 0;
        public int fees     = 0;
        public List<String> errors = new ArrayList<>();
    }
}

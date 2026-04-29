package com.akshat.college_project.service;

import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.dto.SupervisorImportResponse;
import com.akshat.college_project.entity.Supervisor;
import com.akshat.college_project.entity.enums.SupervisorEnrollStatus;
import com.akshat.college_project.repository.SupervisorRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class SupervisorImportService {

    private final SupervisorRepository supervisorRepository;

    public SupervisorImportService(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    @Transactional
    public SupervisorImportResponse importSupervisors(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        List<SupervisorData> parsedData;

        if (filename.endsWith(".csv")) {
            parsedData = parseCsv(file);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            parsedData = parseExcel(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload .csv or .xlsx");
        }

        int total = parsedData.size();
        List<SupervisorImportResponse.ImportError> errors = new ArrayList<>();
        List<Supervisor> supervisorsToSave = new ArrayList<>();
        java.util.Set<String> fileEmails = new java.util.HashSet<>();

        for (int i = 0; i < parsedData.size(); i++) {
            SupervisorData data = parsedData.get(i);
            int rowNum = i + 2; // Assuming row 1 is header

            if (data.name == null || data.name.trim().isEmpty() ||
                data.email == null || data.email.trim().isEmpty() ||
                data.branch == null || data.branch.trim().isEmpty()) {
                errors.add(new SupervisorImportResponse.ImportError(rowNum, "Missing required fields (name, email, branch)."));
                continue;
            }

            String emailClean = data.email.trim().toLowerCase();

            if (!fileEmails.add(emailClean)) {
                errors.add(new SupervisorImportResponse.ImportError(rowNum, "Duplicate email inside the file: " + emailClean));
                continue;
            }

            if (supervisorRepository.existsByMailIgnoreCase(emailClean)) {
                errors.add(new SupervisorImportResponse.ImportError(rowNum, "Duplicate email in database: " + emailClean));
                continue;
            }

            try {
                supervisorsToSave.add(buildSupervisor(data));
            } catch (Exception e) {
                errors.add(new SupervisorImportResponse.ImportError(rowNum, "Error preparing data: " + e.getMessage()));
            }
        }

        if (errors.isEmpty()) {
            supervisorRepository.saveAll(supervisorsToSave);
            return new SupervisorImportResponse(total, total, 0, errors);
        } else {
            return new SupervisorImportResponse(total, 0, errors.size(), errors);
        }
    }

    private Supervisor buildSupervisor(SupervisorData data) {
        Supervisor supervisor = new Supervisor();
        supervisor.setSupervisorId(IdGenerator.generate("sup_"));
        supervisor.setName(data.name.trim());
        supervisor.setMail(data.email.trim().toLowerCase());
        supervisor.setBranch(data.branch.trim().toUpperCase());
        supervisor.setPassword("DEFAULT_PASSWORD_123"); // Required by schema
        supervisor.setEnrollStatus(SupervisorEnrollStatus.ACTIVE); // Set to ACTIVE by default
        supervisor.setPerformanceScore(100.0);
        supervisor.setOtpVerified(false);
        
        return supervisor;
    }

    private List<SupervisorData> parseCsv(MultipartFile file) throws Exception {
        List<SupervisorData> list = new ArrayList<>();
        CSVFormat format = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).setTrim(true).build();
        try (CSVParser parser = new CSVParser(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8), format)) {
            List<String> headers = parser.getHeaderNames();
            String nameHeader = findHeader(headers, "name");
            String emailHeader = findHeader(headers, "email", "mail");
            String branchHeader = findHeader(headers, "branch", "dept", "department");

            for (CSVRecord record : parser) {
                SupervisorData sd = new SupervisorData();
                sd.name = nameHeader != null ? record.get(nameHeader) : null;
                sd.email = emailHeader != null ? record.get(emailHeader) : null;
                sd.branch = branchHeader != null ? record.get(branchHeader) : null;
                list.add(sd);
            }
        }
        return list;
    }

    private List<SupervisorData> parseExcel(MultipartFile file) throws Exception {
        List<SupervisorData> list = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return list;

            int nameIndex = -1, emailIndex = -1, branchIndex = -1;
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().toLowerCase().trim();
                if (header.contains("name")) nameIndex = cell.getColumnIndex();
                else if (header.contains("mail") || header.contains("email")) emailIndex = cell.getColumnIndex();
                else if (header.contains("branch") || header.contains("dept") || header.contains("department")) branchIndex = cell.getColumnIndex();
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                SupervisorData sd = new SupervisorData();
                if (nameIndex != -1) sd.name = getCellValue(row.getCell(nameIndex));
                if (emailIndex != -1) sd.email = getCellValue(row.getCell(emailIndex));
                if (branchIndex != -1) sd.branch = getCellValue(row.getCell(branchIndex));
                list.add(sd);
            }
        }
        return list;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return null;
    }

    private String findHeader(List<String> headers, String... targets) {
        for (String target : targets) {
            for (String h : headers) {
                if (h.toLowerCase().trim().contains(target)) return h;
            }
        }
        return null;
    }

    private static class SupervisorData {
        String name;
        String email;
        String branch;
    }
}

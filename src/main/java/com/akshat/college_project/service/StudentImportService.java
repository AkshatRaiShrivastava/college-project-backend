package com.akshat.college_project.service;

import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.dto.StudentImportResponse;
import com.akshat.college_project.entity.Student;
import com.akshat.college_project.entity.enums.StudentEnrollStatus;
import com.akshat.college_project.repository.StudentRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StudentImportService {

    private final StudentRepository studentRepository;
    private static final Pattern ROLL_NO_PATTERN = Pattern.compile("^(\\d{2})([A-Za-z]+)(\\d+)$");

    public StudentImportService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentImportResponse importStudents(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        List<StudentData> parsedData;

        if (filename.endsWith(".csv")) {
            parsedData = parseCsv(file);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            parsedData = parseExcel(file);
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload .csv or .xlsx");
        }

        int total = parsedData.size();
        List<StudentImportResponse.ImportError> errors = new ArrayList<>();
        List<Student> studentsToSave = new ArrayList<>();
        java.util.Set<String> fileEmails = new java.util.HashSet<>();
        java.util.Set<String> fileRollNos = new java.util.HashSet<>();

        for (int i = 0; i < parsedData.size(); i++) {
            StudentData data = parsedData.get(i);
            int rowNum = i + 2; // Assuming row 1 is header

            if (data.name == null || data.name.trim().isEmpty() ||
                data.rollNo == null || data.rollNo.trim().isEmpty() ||
                data.email == null || data.email.trim().isEmpty()) {
                errors.add(new StudentImportResponse.ImportError(rowNum, "Missing required fields (name, roll_number, email)."));
                continue;
            }

            String emailClean = data.email.trim().toLowerCase();
            String rollClean = data.rollNo.trim().toUpperCase();

            if (!fileEmails.add(emailClean)) {
                errors.add(new StudentImportResponse.ImportError(rowNum, "Duplicate email inside the file: " + emailClean));
                continue;
            }
            if (!fileRollNos.add(rollClean)) {
                errors.add(new StudentImportResponse.ImportError(rowNum, "Duplicate roll number inside the file: " + rollClean));
                continue;
            }

            if (studentRepository.existsByMailIgnoreCase(emailClean) || studentRepository.existsByRollNo(rollClean)) {
                errors.add(new StudentImportResponse.ImportError(rowNum, "Duplicate email or roll number in database."));
                continue;
            }

            try {
                studentsToSave.add(buildStudent(data));
            } catch (Exception e) {
                errors.add(new StudentImportResponse.ImportError(rowNum, "Error preparing data: " + e.getMessage()));
            }
        }

        if (errors.isEmpty()) {
            studentRepository.saveAll(studentsToSave);
            return new StudentImportResponse(total, total, 0, errors);
        } else {
            return new StudentImportResponse(total, 0, errors.size(), errors);
        }
    }

    private Student buildStudent(StudentData data) {
        Student student = new Student();
        student.setStudentId(IdGenerator.generate("stu_"));
        student.setName(data.name.trim());
        student.setMail(data.email.trim().toLowerCase());
        student.setRollNo(data.rollNo.trim().toUpperCase());
        student.setPassword("DEFAULT_PASSWORD_123"); // Required by schema
        student.setEnrollStatus(StudentEnrollStatus.PENDING); // Set to PENDING by default
        student.setPerformanceScore(50.0);
        student.setOtpVerified(false);
        
        Matcher matcher = ROLL_NO_PATTERN.matcher(student.getRollNo());
        if (matcher.matches()) {
            student.setBatch("20" + matcher.group(1));
            student.setBranch(matcher.group(2).toUpperCase());
        } else {
            student.setBatch("UNKNOWN");
            student.setBranch("UNKNOWN");
        }
        return student;
    }

    private List<StudentData> parseCsv(MultipartFile file) throws Exception {
        List<StudentData> list = new ArrayList<>();
        CSVFormat format = CSVFormat.Builder.create().setHeader().setSkipHeaderRecord(true).setTrim(true).build();
        try (CSVParser parser = new CSVParser(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8), format)) {
            List<String> headers = parser.getHeaderNames();
            String nameHeader = findHeader(headers, "name");
            String rollHeader = findHeader(headers, "roll_number", "rollno", "roll");
            String emailHeader = findHeader(headers, "email", "mail");

            for (CSVRecord record : parser) {
                StudentData sd = new StudentData();
                sd.name = nameHeader != null ? record.get(nameHeader) : null;
                sd.rollNo = rollHeader != null ? record.get(rollHeader) : null;
                sd.email = emailHeader != null ? record.get(emailHeader) : null;
                list.add(sd);
            }
        }
        return list;
    }

    private List<StudentData> parseExcel(MultipartFile file) throws Exception {
        List<StudentData> list = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return list;

            int nameIndex = -1, rollIndex = -1, emailIndex = -1;
            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().toLowerCase().trim();
                if (header.contains("name")) nameIndex = cell.getColumnIndex();
                else if (header.contains("roll")) rollIndex = cell.getColumnIndex();
                else if (header.contains("mail")) emailIndex = cell.getColumnIndex();
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                StudentData sd = new StudentData();
                if (nameIndex != -1) sd.name = getCellValue(row.getCell(nameIndex));
                if (rollIndex != -1) sd.rollNo = getCellValue(row.getCell(rollIndex));
                if (emailIndex != -1) sd.email = getCellValue(row.getCell(emailIndex));
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

    private static class StudentData {
        String name;
        String rollNo;
        String email;
    }
}

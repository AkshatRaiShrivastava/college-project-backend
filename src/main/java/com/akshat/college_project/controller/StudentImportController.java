package com.akshat.college_project.controller;

import com.akshat.college_project.dto.StudentImportResponse;
import com.akshat.college_project.service.StudentImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class StudentImportController {

    private final StudentImportService studentImportService;

    public StudentImportController(StudentImportService studentImportService) {
        this.studentImportService = studentImportService;
    }

    @PostMapping("/import-students")
    public ResponseEntity<StudentImportResponse> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            StudentImportResponse response = studentImportService.importStudents(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            StudentImportResponse errorResponse = new StudentImportResponse();
            errorResponse.setFailed(1);
            errorResponse.setErrors(java.util.List.of(new StudentImportResponse.ImportError(0, "Failed to parse file: " + e.getMessage())));
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

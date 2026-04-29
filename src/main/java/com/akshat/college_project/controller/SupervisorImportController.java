package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SupervisorImportResponse;
import com.akshat.college_project.service.SupervisorImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class SupervisorImportController {

    private final SupervisorImportService supervisorImportService;

    public SupervisorImportController(SupervisorImportService supervisorImportService) {
        this.supervisorImportService = supervisorImportService;
    }

    @PostMapping("/import-supervisors")
    public ResponseEntity<SupervisorImportResponse> importSupervisors(@RequestParam("file") MultipartFile file) {
        try {
            SupervisorImportResponse response = supervisorImportService.importSupervisors(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SupervisorImportResponse errorResponse = new SupervisorImportResponse();
            errorResponse.setFailed(1);
            errorResponse.setErrors(java.util.List.of(new SupervisorImportResponse.ImportError(0, "Failed to parse file: " + e.getMessage())));
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}

package com.akshat.college_project.controller;

import com.akshat.college_project.service.OneDriveStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final OneDriveStorageService oneDriveStorageService;

    public FileController(OneDriveStorageService oneDriveStorageService) {
        this.oneDriveStorageService = oneDriveStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("projectId") String projectId,
            @RequestParam("stage") String stage
    ) {
        try {
            String fileUrl = oneDriveStorageService.uploadFile(file, projectId, stage);
            return ResponseEntity.ok(Map.of(
                    "fileUrl", fileUrl,
                    "fileName", file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.bin",
                    "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}

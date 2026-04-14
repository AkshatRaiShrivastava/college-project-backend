package com.akshat.college_project.controller;

import com.akshat.college_project.dto.AdminCreateRequest;
import com.akshat.college_project.dto.AdminUpdateRequest;
import com.akshat.college_project.entity.Admin;
import com.akshat.college_project.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<Admin> create(@Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.create(request));
    }

    @GetMapping("/{adminId}")
    public Admin get(@PathVariable String adminId) {
        return adminService.get(adminId);
    }

    @GetMapping
    public List<Admin> getAll() {
        return adminService.getAll();
    }

    @PutMapping("/{adminId}")
    public Admin update(@PathVariable String adminId, @RequestBody AdminUpdateRequest request) {
        return adminService.update(adminId, request);
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<Void> delete(@PathVariable String adminId) {
        adminService.delete(adminId);
        return ResponseEntity.noContent().build();
    }
}

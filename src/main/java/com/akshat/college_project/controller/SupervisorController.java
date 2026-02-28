package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SupervisorCreateRequest;
import com.akshat.college_project.dto.SupervisorUpdateRequest;
import com.akshat.college_project.entity.Supervisor;
import com.akshat.college_project.service.SupervisorService;
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
@RequestMapping("/api/supervisors")
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @PostMapping
    public ResponseEntity<Supervisor> create(@Valid @RequestBody SupervisorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supervisorService.create(request));
    }

    @GetMapping("/{supervisorId}")
    public Supervisor get(@PathVariable String supervisorId) {
        return supervisorService.get(supervisorId);
    }

    @GetMapping
    public List<Supervisor> getAll() {
        return supervisorService.getAll();
    }

    @PutMapping("/{supervisorId}")
    public Supervisor update(@PathVariable String supervisorId, @RequestBody SupervisorUpdateRequest request) {
        return supervisorService.update(supervisorId, request);
    }

    @DeleteMapping("/{supervisorId}")
    public ResponseEntity<Void> delete(@PathVariable String supervisorId) {
        supervisorService.delete(supervisorId);
        return ResponseEntity.noContent().build();
    }
}

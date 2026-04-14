package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SupervisorRequestCreateRequest;
import com.akshat.college_project.dto.SupervisorRequestUpdateRequest;
import com.akshat.college_project.entity.SupervisorRequest;
import com.akshat.college_project.entity.enums.RequestStatus;
import com.akshat.college_project.service.SupervisorRequestService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class SupervisorRequestController {

    private final SupervisorRequestService supervisorRequestService;

    public SupervisorRequestController(SupervisorRequestService supervisorRequestService) {
        this.supervisorRequestService = supervisorRequestService;
    }

    @PostMapping
    public ResponseEntity<SupervisorRequest> create(@Valid @RequestBody SupervisorRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supervisorRequestService.create(request));
    }

    @GetMapping("/{requestId}")
    public SupervisorRequest get(@PathVariable Long requestId) {
        return supervisorRequestService.get(requestId);
    }

    @GetMapping
    public List<SupervisorRequest> getAll(@RequestParam(required = false) RequestStatus status) {
        return supervisorRequestService.getAll(status);
    }

    @PutMapping("/{requestId}")
    public SupervisorRequest update(@PathVariable Long requestId, @RequestBody SupervisorRequestUpdateRequest request) {
        return supervisorRequestService.update(requestId, request);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> delete(@PathVariable Long requestId) {
        supervisorRequestService.delete(requestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<SupervisorRequest> approve(@PathVariable Long requestId, @RequestBody java.util.Map<String, String> payload) {
        String adminId = payload.get("adminId");
        return ResponseEntity.ok(supervisorRequestService.approveRequest(requestId, adminId));
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<SupervisorRequest> reject(@PathVariable Long requestId, @RequestBody java.util.Map<String, String> payload) {
        String reason = payload.get("reason");
        return ResponseEntity.ok(supervisorRequestService.rejectRequest(requestId, reason));
    }
}

package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.SynopsisSubmission;
import com.akshat.college_project.service.submission.SynopsisSubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/submissions/synopsis")
public class SynopsisSubmissionController {

    private final SynopsisSubmissionService synopsisSubmissionService;

    public SynopsisSubmissionController(SynopsisSubmissionService synopsisSubmissionService) {
        this.synopsisSubmissionService = synopsisSubmissionService;
    }

    @PostMapping
    public ResponseEntity<SynopsisSubmission> create(@Valid @RequestBody SubmissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(synopsisSubmissionService.create(request));
    }

    @GetMapping("/{submissionId}")
    public SynopsisSubmission get(@PathVariable String submissionId) {
        return synopsisSubmissionService.get(submissionId);
    }

    @GetMapping
    public List<SynopsisSubmission> getAll() {
        return synopsisSubmissionService.getAll();
    }

    @GetMapping("/document/{documentId}")
    public List<SynopsisSubmission> getByDocument(@PathVariable String documentId) {
        return synopsisSubmissionService.getByDocument(documentId);
    }

    @PatchMapping("/{submissionId}/review")
    public SynopsisSubmission review(
            @PathVariable String submissionId,
            @Valid @RequestBody SubmissionReviewRequest request
    ) {
        return synopsisSubmissionService.review(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(@PathVariable String submissionId) {
        synopsisSubmissionService.delete(submissionId);
        return ResponseEntity.noContent().build();
    }
}

package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.FinalSubmission;
import com.akshat.college_project.service.submission.FinalSubmissionService;
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
@RequestMapping("/api/submissions/final")
public class FinalSubmissionController {

    private final FinalSubmissionService finalSubmissionService;

    public FinalSubmissionController(FinalSubmissionService finalSubmissionService) {
        this.finalSubmissionService = finalSubmissionService;
    }

    @PostMapping
    public ResponseEntity<FinalSubmission> create(@Valid @RequestBody SubmissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(finalSubmissionService.create(request));
    }

    @GetMapping("/{submissionId}")
    public FinalSubmission get(@PathVariable String submissionId) {
        return finalSubmissionService.get(submissionId);
    }

    @GetMapping
    public List<FinalSubmission> getAll() {
        return finalSubmissionService.getAll();
    }

    @GetMapping("/document/{documentId}")
    public List<FinalSubmission> getByDocument(@PathVariable String documentId) {
        return finalSubmissionService.getByDocument(documentId);
    }

    @PatchMapping("/{submissionId}/review")
    public FinalSubmission review(
            @PathVariable String submissionId,
            @Valid @RequestBody SubmissionReviewRequest request
    ) {
        return finalSubmissionService.review(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(@PathVariable String submissionId) {
        finalSubmissionService.delete(submissionId);
        return ResponseEntity.noContent().build();
    }
}

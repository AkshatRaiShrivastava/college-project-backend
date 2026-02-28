package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.Progress1Submission;
import com.akshat.college_project.service.submission.Progress1SubmissionService;
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
@RequestMapping("/api/submissions/progress1")
public class Progress1SubmissionController {

    private final Progress1SubmissionService progress1SubmissionService;

    public Progress1SubmissionController(Progress1SubmissionService progress1SubmissionService) {
        this.progress1SubmissionService = progress1SubmissionService;
    }

    @PostMapping
    public ResponseEntity<Progress1Submission> create(@Valid @RequestBody SubmissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(progress1SubmissionService.create(request));
    }

    @GetMapping("/{submissionId}")
    public Progress1Submission get(@PathVariable String submissionId) {
        return progress1SubmissionService.get(submissionId);
    }

    @GetMapping
    public List<Progress1Submission> getAll() {
        return progress1SubmissionService.getAll();
    }

    @GetMapping("/document/{documentId}")
    public List<Progress1Submission> getByDocument(@PathVariable String documentId) {
        return progress1SubmissionService.getByDocument(documentId);
    }

    @PatchMapping("/{submissionId}/review")
    public Progress1Submission review(
            @PathVariable String submissionId,
            @Valid @RequestBody SubmissionReviewRequest request
    ) {
        return progress1SubmissionService.review(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(@PathVariable String submissionId) {
        progress1SubmissionService.delete(submissionId);
        return ResponseEntity.noContent().build();
    }
}

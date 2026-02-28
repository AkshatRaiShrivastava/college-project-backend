package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.Progress2Submission;
import com.akshat.college_project.service.submission.Progress2SubmissionService;
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
@RequestMapping("/api/submissions/progress2")
public class Progress2SubmissionController {

    private final Progress2SubmissionService progress2SubmissionService;

    public Progress2SubmissionController(Progress2SubmissionService progress2SubmissionService) {
        this.progress2SubmissionService = progress2SubmissionService;
    }

    @PostMapping
    public ResponseEntity<Progress2Submission> create(@Valid @RequestBody SubmissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(progress2SubmissionService.create(request));
    }

    @GetMapping("/{submissionId}")
    public Progress2Submission get(@PathVariable String submissionId) {
        return progress2SubmissionService.get(submissionId);
    }

    @GetMapping
    public List<Progress2Submission> getAll() {
        return progress2SubmissionService.getAll();
    }

    @GetMapping("/document/{documentId}")
    public List<Progress2Submission> getByDocument(@PathVariable String documentId) {
        return progress2SubmissionService.getByDocument(documentId);
    }

    @PatchMapping("/{submissionId}/review")
    public Progress2Submission review(
            @PathVariable String submissionId,
            @Valid @RequestBody SubmissionReviewRequest request
    ) {
        return progress2SubmissionService.review(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(@PathVariable String submissionId) {
        progress2SubmissionService.delete(submissionId);
        return ResponseEntity.noContent().build();
    }
}

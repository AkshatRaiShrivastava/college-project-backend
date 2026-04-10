package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SupervisorSubmissionReviewRequest;
import com.akshat.college_project.dto.TeamSubmissionReviewRequest;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseEntity<SynopsisSubmission> create(
            @Valid @RequestBody SubmissionCreateRequest request,
            @RequestHeader("X-User-ID") String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(synopsisSubmissionService.create(request, userId));
    }

    @GetMapping("/{submissionId}")
    public SynopsisSubmission get(@PathVariable String submissionId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return synopsisSubmissionService.get(submissionId, userId);
    }

    @GetMapping
    public List<SynopsisSubmission> getAll(@RequestHeader(value = "X-User-ID", required = false) String userId) {
        return synopsisSubmissionService.getAll(userId);
    }

    @GetMapping("/document/{documentId}")
    public List<SynopsisSubmission> getByDocument(@PathVariable String documentId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return synopsisSubmissionService.getByDocument(documentId, userId);
    }

    @PatchMapping("/{submissionId}/team-review")
    public SynopsisSubmission teamReview(
            @PathVariable String submissionId,
            @Valid @RequestBody TeamSubmissionReviewRequest request
    ) {
        return synopsisSubmissionService.teamReview(submissionId, request);
    }

    @PatchMapping("/{submissionId}/supervisor-review")
    public SynopsisSubmission supervisorReview(
            @PathVariable String submissionId,
            @Valid @RequestBody SupervisorSubmissionReviewRequest request
    ) {
        return synopsisSubmissionService.supervisorReview(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(@PathVariable String submissionId) {
        synopsisSubmissionService.delete(submissionId);
        return ResponseEntity.noContent().build();
    }
}

package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SupervisorSubmissionReviewRequest;
import com.akshat.college_project.dto.TeamSubmissionReviewRequest;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseEntity<FinalSubmission> create(
            @Valid @RequestBody SubmissionCreateRequest request,
            @RequestHeader("X-User-ID") String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(finalSubmissionService.create(request, userId));
    }

    @GetMapping("/{submissionId}")
    public FinalSubmission get(@PathVariable String submissionId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return finalSubmissionService.get(submissionId, userId);
    }

    @GetMapping
    public List<FinalSubmission> getAll(@RequestHeader(value = "X-User-ID", required = false) String userId) {
        return finalSubmissionService.getAll(userId);
    }

    @GetMapping("/document/{documentId}")
    public List<FinalSubmission> getByDocument(@PathVariable String documentId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return finalSubmissionService.getByDocument(documentId, userId);
    }

    @PatchMapping("/{submissionId}/team-review")
    public FinalSubmission teamReview(
            @PathVariable String submissionId,
            @Valid @RequestBody TeamSubmissionReviewRequest request
    ) {
        return finalSubmissionService.teamReview(submissionId, request);
    }

    @PatchMapping("/{submissionId}/supervisor-review")
    public FinalSubmission supervisorReview(
            @PathVariable String submissionId,
            @Valid @RequestBody SupervisorSubmissionReviewRequest request
    ) {
        return finalSubmissionService.supervisorReview(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(
            @PathVariable String submissionId,
            @RequestHeader("X-User-ID") String userId
    ) {
        finalSubmissionService.delete(submissionId, userId);
        return ResponseEntity.noContent().build();
    }
}

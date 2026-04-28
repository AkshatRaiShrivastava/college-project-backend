package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SupervisorSubmissionReviewRequest;
import com.akshat.college_project.dto.TeamSubmissionReviewRequest;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseEntity<Progress1Submission> create(
            @Valid @RequestBody SubmissionCreateRequest request,
            @RequestHeader("X-User-ID") String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(progress1SubmissionService.create(request, userId));
    }

    @GetMapping("/{submissionId}")
    public Progress1Submission get(@PathVariable String submissionId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return progress1SubmissionService.get(submissionId, userId);
    }

    @GetMapping
    public List<Progress1Submission> getAll(@RequestHeader(value = "X-User-ID", required = false) String userId) {
        return progress1SubmissionService.getAll(userId);
    }

    @GetMapping("/document/{documentId}")
    public List<Progress1Submission> getByDocument(@PathVariable String documentId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return progress1SubmissionService.getByDocument(documentId, userId);
    }

    @PatchMapping("/{submissionId}/team-review")
    public Progress1Submission teamReview(
            @PathVariable String submissionId,
            @Valid @RequestBody TeamSubmissionReviewRequest request
    ) {
        return progress1SubmissionService.teamReview(submissionId, request);
    }

    @PatchMapping("/{submissionId}/supervisor-review")
    public Progress1Submission supervisorReview(
            @PathVariable String submissionId,
            @Valid @RequestBody SupervisorSubmissionReviewRequest request
    ) {
        return progress1SubmissionService.supervisorReview(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(
            @PathVariable String submissionId,
            @RequestHeader("X-User-ID") String userId
    ) {
        progress1SubmissionService.delete(submissionId, userId);
        return ResponseEntity.noContent().build();
    }
}

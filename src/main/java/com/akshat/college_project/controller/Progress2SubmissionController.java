package com.akshat.college_project.controller;

import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SupervisorSubmissionReviewRequest;
import com.akshat.college_project.dto.TeamSubmissionReviewRequest;
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
import org.springframework.web.bind.annotation.RequestHeader;
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
    public ResponseEntity<Progress2Submission> create(
            @Valid @RequestBody SubmissionCreateRequest request,
            @RequestHeader("X-User-ID") String userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(progress2SubmissionService.create(request, userId));
    }

    @GetMapping("/{submissionId}")
    public Progress2Submission get(@PathVariable String submissionId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return progress2SubmissionService.get(submissionId, userId);
    }

    @GetMapping
    public List<Progress2Submission> getAll(@RequestHeader(value = "X-User-ID", required = false) String userId) {
        return progress2SubmissionService.getAll(userId);
    }

    @GetMapping("/document/{documentId}")
    public List<Progress2Submission> getByDocument(@PathVariable String documentId, @RequestHeader(value = "X-User-ID", required = false) String userId) {
        return progress2SubmissionService.getByDocument(documentId, userId);
    }

    @PatchMapping("/{submissionId}/team-review")
    public Progress2Submission teamReview(
            @PathVariable String submissionId,
            @Valid @RequestBody TeamSubmissionReviewRequest request
    ) {
        return progress2SubmissionService.teamReview(submissionId, request);
    }

    @PatchMapping("/{submissionId}/supervisor-review")
    public Progress2Submission supervisorReview(
            @PathVariable String submissionId,
            @Valid @RequestBody SupervisorSubmissionReviewRequest request
    ) {
        return progress2SubmissionService.supervisorReview(submissionId, request);
    }

    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> delete(@PathVariable String submissionId) {
        progress2SubmissionService.delete(submissionId);
        return ResponseEntity.noContent().build();
    }
}

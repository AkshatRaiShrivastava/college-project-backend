package com.akshat.college_project.service.submission;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.Progress1Submission;
import com.akshat.college_project.entity.enums.CommentByRole;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.Progress1SubmissionRepository;
import com.akshat.college_project.service.DocumentService;
import com.akshat.college_project.service.ReferenceValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Progress1SubmissionService {

    private final Progress1SubmissionRepository repository;
    private final ReferenceValidator referenceValidator;
    private final DocumentService documentService;

    public Progress1SubmissionService(
            Progress1SubmissionRepository repository,
            ReferenceValidator referenceValidator,
            DocumentService documentService
    ) {
        this.repository = repository;
        this.referenceValidator = referenceValidator;
        this.documentService = documentService;
    }

    public Progress1Submission create(SubmissionCreateRequest request) {
        referenceValidator.requireDocument(request.documentId());

        Progress1Submission submission = new Progress1Submission();
        submission.setProgress1Id(IdGenerator.generate("pg1_"));
        submission.setDocumentId(request.documentId());
        submission.setComment(request.comment());
        submission.setFileUrl(request.fileUrl());
        submission.setFileName(request.fileName());

        Progress1Submission saved = repository.save(submission);
        documentService.appendSubmissionId(saved.getDocumentId(), StageStatus.PROGRESS1, saved.getProgress1Id());
        return saved;
    }

    public Progress1Submission get(String submissionId) {
        return repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress1 submission not found: " + submissionId));
    }

    public List<Progress1Submission> getAll() {
        return repository.findAll();
    }

    public List<Progress1Submission> getByDocument(String documentId) {
        referenceValidator.requireDocument(documentId);
        return repository.findByDocumentId(documentId);
    }

    public Progress1Submission review(String submissionId, SubmissionReviewRequest request) {
        Progress1Submission submission = get(submissionId);
        validateReviewer(request.commentByRole(), request.commentById());

        submission.setComment(request.comment());
        submission.setStatus(request.status());
        submission.setCommentByRole(request.commentByRole());
        submission.setCommentById(request.commentById());
        return repository.save(submission);
    }

    public void delete(String submissionId) {
        Progress1Submission submission = get(submissionId);
        repository.delete(submission);
        documentService.removeSubmissionId(submission.getDocumentId(), StageStatus.PROGRESS1, submission.getProgress1Id());
    }

    private void validateReviewer(CommentByRole role, String roleId) {
        if (role == null && roleId == null) {
            return;
        }
        if (role == null || roleId == null) {
            throw new BadRequestException("Both commentByRole and commentById are required together");
        }
        if (role == CommentByRole.SUPERVISOR) {
            referenceValidator.requireSupervisor(roleId);
        } else if (role == CommentByRole.ADMIN) {
            referenceValidator.requireAdmin(roleId);
        }
    }
}

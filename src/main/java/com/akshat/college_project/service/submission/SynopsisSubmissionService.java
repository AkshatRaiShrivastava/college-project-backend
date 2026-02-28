package com.akshat.college_project.service.submission;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.SynopsisSubmission;
import com.akshat.college_project.entity.enums.CommentByRole;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.SynopsisSubmissionRepository;
import com.akshat.college_project.service.DocumentService;
import com.akshat.college_project.service.ReferenceValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SynopsisSubmissionService {

    private final SynopsisSubmissionRepository repository;
    private final ReferenceValidator referenceValidator;
    private final DocumentService documentService;

    public SynopsisSubmissionService(
            SynopsisSubmissionRepository repository,
            ReferenceValidator referenceValidator,
            DocumentService documentService
    ) {
        this.repository = repository;
        this.referenceValidator = referenceValidator;
        this.documentService = documentService;
    }

    public SynopsisSubmission create(SubmissionCreateRequest request) {
        referenceValidator.requireDocument(request.documentId());

        SynopsisSubmission submission = new SynopsisSubmission();
        submission.setSynopsisId(IdGenerator.generate("syn_"));
        submission.setDocumentId(request.documentId());
        submission.setComment(request.comment());

        SynopsisSubmission saved = repository.save(submission);
        documentService.appendSubmissionId(saved.getDocumentId(), StageStatus.SYNOPSIS, saved.getSynopsisId());
        return saved;
    }

    public SynopsisSubmission get(String submissionId) {
        return repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Synopsis submission not found: " + submissionId));
    }

    public List<SynopsisSubmission> getAll() {
        return repository.findAll();
    }

    public List<SynopsisSubmission> getByDocument(String documentId) {
        referenceValidator.requireDocument(documentId);
        return repository.findByDocumentId(documentId);
    }

    public SynopsisSubmission review(String submissionId, SubmissionReviewRequest request) {
        SynopsisSubmission submission = get(submissionId);
        validateReviewer(request.commentByRole(), request.commentById());

        submission.setComment(request.comment());
        submission.setStatus(request.status());
        submission.setCommentByRole(request.commentByRole());
        submission.setCommentById(request.commentById());
        return repository.save(submission);
    }

    public void delete(String submissionId) {
        SynopsisSubmission submission = get(submissionId);
        repository.delete(submission);
        documentService.removeSubmissionId(submission.getDocumentId(), StageStatus.SYNOPSIS, submission.getSynopsisId());
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

package com.akshat.college_project.service.submission;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.FinalSubmission;
import com.akshat.college_project.entity.enums.CommentByRole;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.FinalSubmissionRepository;
import com.akshat.college_project.service.DocumentService;
import com.akshat.college_project.service.ReferenceValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalSubmissionService {

    private final FinalSubmissionRepository repository;
    private final ReferenceValidator referenceValidator;
    private final DocumentService documentService;

    public FinalSubmissionService(
            FinalSubmissionRepository repository,
            ReferenceValidator referenceValidator,
            DocumentService documentService
    ) {
        this.repository = repository;
        this.referenceValidator = referenceValidator;
        this.documentService = documentService;
    }

    public FinalSubmission create(SubmissionCreateRequest request) {
        referenceValidator.requireDocument(request.documentId());

        FinalSubmission submission = new FinalSubmission();
        submission.setFinalId(IdGenerator.generate("fnl_"));
        submission.setDocumentId(request.documentId());
        submission.setComment(request.comment());
        submission.setFileUrl(request.fileUrl());
        submission.setFileName(request.fileName());

        FinalSubmission saved = repository.save(submission);
        documentService.appendSubmissionId(saved.getDocumentId(), StageStatus.FINAL, saved.getFinalId());
        return saved;
    }

    public FinalSubmission get(String submissionId) {
        return repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Final submission not found: " + submissionId));
    }

    public List<FinalSubmission> getAll() {
        return repository.findAll();
    }

    public List<FinalSubmission> getByDocument(String documentId) {
        referenceValidator.requireDocument(documentId);
        return repository.findByDocumentId(documentId);
    }

    public FinalSubmission review(String submissionId, SubmissionReviewRequest request) {
        FinalSubmission submission = get(submissionId);
        validateReviewer(request.commentByRole(), request.commentById());

        submission.setComment(request.comment());
        submission.setStatus(request.status());
        submission.setCommentByRole(request.commentByRole());
        submission.setCommentById(request.commentById());
        return repository.save(submission);
    }

    public void delete(String submissionId) {
        FinalSubmission submission = get(submissionId);
        repository.delete(submission);
        documentService.removeSubmissionId(submission.getDocumentId(), StageStatus.FINAL, submission.getFinalId());
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

package com.akshat.college_project.service.submission;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SubmissionReviewRequest;
import com.akshat.college_project.entity.Progress2Submission;
import com.akshat.college_project.entity.enums.CommentByRole;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.Progress2SubmissionRepository;
import com.akshat.college_project.service.DocumentService;
import com.akshat.college_project.service.ReferenceValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Progress2SubmissionService {

    private final Progress2SubmissionRepository repository;
    private final ReferenceValidator referenceValidator;
    private final DocumentService documentService;

    public Progress2SubmissionService(
            Progress2SubmissionRepository repository,
            ReferenceValidator referenceValidator,
            DocumentService documentService
    ) {
        this.repository = repository;
        this.referenceValidator = referenceValidator;
        this.documentService = documentService;
    }

    public Progress2Submission create(SubmissionCreateRequest request) {
        referenceValidator.requireDocument(request.documentId());

        Progress2Submission submission = new Progress2Submission();
        submission.setProgress2Id(IdGenerator.generate("p2_"));
        submission.setDocumentId(request.documentId());
        submission.setComment(request.comment());

        Progress2Submission saved = repository.save(submission);
        documentService.appendSubmissionId(saved.getDocumentId(), StageStatus.PROGRESS2, saved.getProgress2Id());
        return saved;
    }

    public Progress2Submission get(String submissionId) {
        return repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress2 submission not found: " + submissionId));
    }

    public List<Progress2Submission> getAll() {
        return repository.findAll();
    }

    public List<Progress2Submission> getByDocument(String documentId) {
        referenceValidator.requireDocument(documentId);
        return repository.findByDocumentId(documentId);
    }

    public Progress2Submission review(String submissionId, SubmissionReviewRequest request) {
        Progress2Submission submission = get(submissionId);
        validateReviewer(request.commentByRole(), request.commentById());

        submission.setComment(request.comment());
        submission.setStatus(request.status());
        submission.setCommentByRole(request.commentByRole());
        submission.setCommentById(request.commentById());
        return repository.save(submission);
    }

    public void delete(String submissionId) {
        Progress2Submission submission = get(submissionId);
        repository.delete(submission);
        documentService.removeSubmissionId(submission.getDocumentId(), StageStatus.PROGRESS2, submission.getProgress2Id());
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

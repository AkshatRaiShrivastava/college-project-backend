package com.akshat.college_project.service.submission;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SupervisorSubmissionReviewRequest;
import com.akshat.college_project.dto.TeamReviewVote;
import com.akshat.college_project.dto.TeamSubmissionReviewRequest;
import com.akshat.college_project.entity.FinalSubmission;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.entity.enums.SubmissionStatus;
import com.akshat.college_project.repository.FinalSubmissionRepository;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.service.DocumentService;
import com.akshat.college_project.service.ReferenceValidator;
import com.akshat.college_project.service.SubmissionWorkflowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FinalSubmissionService {

    private final FinalSubmissionRepository repository;
    private final ReferenceValidator referenceValidator;
    private final DocumentService documentService;
    private final SubmissionWorkflowService workflowService;
    private final ProjectRepository projectRepository;

    public FinalSubmissionService(
            FinalSubmissionRepository repository,
            ReferenceValidator referenceValidator,
            DocumentService documentService,
            SubmissionWorkflowService workflowService,
            ProjectRepository projectRepository
    ) {
        this.repository = repository;
        this.referenceValidator = referenceValidator;
        this.documentService = documentService;
        this.workflowService = workflowService;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public FinalSubmission create(SubmissionCreateRequest request, String leaderId) {
        referenceValidator.requireDocument(request.documentId());
        var document = documentService.get(request.documentId());
        var project = workflowService.requireCurrentStageProject(document.getProjectId(), StageStatus.FINAL);
        workflowService.requireLeader(project.getProjectId(), leaderId);
        workflowService.requireStageWindow(project.getProjectId(), StageStatus.FINAL);

        List<FinalSubmission> existing = repository.findByDocumentId(request.documentId());
        Optional<FinalSubmission> latest = existing.stream()
                .sorted(Comparator.comparing(FinalSubmission::getVersionNo, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(FinalSubmission::getUploadedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .reduce((first, second) -> second);

        FinalSubmission submission = new FinalSubmission();
        submission.setFinalId(IdGenerator.generate("fnl_"));
        submission.setProjectId(project.getProjectId());
        submission.setDocumentId(request.documentId());
        submission.setLeaderId(leaderId);
        submission.setVersionNo(latest.map(value -> value.getVersionNo() + 1).orElse(1));
        submission.setRevisionOf(latest.map(FinalSubmission::getFinalId).orElse(null));
        submission.setComment(request.comment());
        submission.setFileUrl(request.fileUrl());
        submission.setFileName(request.fileName());
        submission.setTeamReviewJson("[]");
        submission.setTeamReviewStatus("PENDING");
        submission.setVisibleToSupervisor(Boolean.FALSE);
        submission.setVisibleToAdmin(Boolean.FALSE);
        submission.setStatus(SubmissionStatus.PENDING);

        FinalSubmission saved = repository.save(submission);
        documentService.appendSubmissionId(saved.getDocumentId(), StageStatus.FINAL, saved.getFinalId());
        return saved;
    }

    public FinalSubmission get(String submissionId, String requesterId) {
        FinalSubmission submission = repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Final submission not found: " + submissionId));
        enforceVisibility(submission, requesterId);
        return submission;
    }

    public List<FinalSubmission> getAll(String requesterId) {
        return repository.findAll().stream().filter(submission -> matchesVisibility(submission, requesterId)).collect(Collectors.toList());
    }

    public List<FinalSubmission> getByDocument(String documentId, String requesterId) {
        try {
            referenceValidator.requireDocument(documentId);
        } catch (Exception ex) {
            return List.of();
        }
        List<FinalSubmission> submissions = repository.findByDocumentId(documentId);
        if (requesterId == null || requesterId.isBlank()) {
            return submissions;
        }
        String role = workflowService.resolveRole(requesterId);
        return switch (role) {
            case "ADMIN" -> submissions;
            case "SUPERVISOR" -> submissions.stream().filter(submission -> canSupervisorAccess(submission, requesterId)).collect(Collectors.toList());
            case "STUDENT" -> submissions;
            default -> submissions;
        };
    }

    @Transactional
    public FinalSubmission teamReview(String submissionId, TeamSubmissionReviewRequest request) {
        FinalSubmission submission = get(submissionId, request.reviewerId());
        workflowService.requireStudentTeamMember(submission.getProjectId(), request.reviewerId());

        List<TeamReviewVote> votes = workflowService.readVotes(submission.getTeamReviewJson());
        votes.removeIf(vote -> request.reviewerId().equals(vote.reviewerId()));
        votes.add(new TeamReviewVote(request.reviewerId(), request.approved(), request.comment(), Instant.now()));

        Set<String> requiredReviewers = new java.util.HashSet<>(workflowService.getTeamMemberIds(submission.getProjectId()));

        boolean anyReject = votes.stream().anyMatch(vote -> Boolean.FALSE.equals(vote.approved()));
        boolean allApproved = votes.stream().filter(vote -> Boolean.TRUE.equals(vote.approved())).map(TeamReviewVote::reviewerId).collect(Collectors.toSet()).containsAll(requiredReviewers);

        submission.setTeamReviewJson(workflowService.writeVotes(votes));
        submission.setComment(request.comment() != null ? request.comment() : submission.getComment());
        if (anyReject) {
            submission.setStatus(SubmissionStatus.REJECTED);
            submission.setTeamReviewStatus("REJECTED");
            submission.setVisibleToSupervisor(Boolean.FALSE);
            submission.setVisibleToAdmin(Boolean.FALSE);
        } else if (allApproved && votes.size() >= requiredReviewers.size()) {
            submission.setStatus(SubmissionStatus.APPROVED);
            submission.setTeamReviewStatus("APPROVED");
            submission.setVisibleToSupervisor(Boolean.TRUE);
        } else {
            submission.setStatus(SubmissionStatus.PENDING);
            submission.setTeamReviewStatus("PENDING");
            submission.setVisibleToSupervisor(Boolean.FALSE);
        }
        return repository.save(submission);
    }

    @Transactional
    public FinalSubmission supervisorReview(String submissionId, SupervisorSubmissionReviewRequest request) {
        FinalSubmission submission = get(submissionId, request.supervisorId());
        workflowService.requireAssignedSupervisor(submission.getProjectId(), request.supervisorId());

        submission.setComment(request.comment() != null ? request.comment() : submission.getComment());
        submission.setVisibleToSupervisor(Boolean.TRUE);
        if (request.approved()) {
            submission.setStatus(SubmissionStatus.APPROVED);
            submission.setVisibleToAdmin(Boolean.TRUE);
            advanceProjectStage(submission.getProjectId(), StageStatus.FINAL);
        } else {
            submission.setStatus(SubmissionStatus.REVISION);
            submission.setVisibleToAdmin(Boolean.FALSE);
        }
        return repository.save(submission);
    }

    public void delete(String submissionId, String requesterId) {
        if (requesterId == null || requesterId.isBlank()) {
            throw new BadRequestException("User id is required to delete submissions");
        }

        FinalSubmission submission = repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Final submission not found: " + submissionId));

        boolean isAdmin = workflowService.isAdmin(requesterId);
        if (!isAdmin) {
            workflowService.requireLeader(submission.getProjectId(), requesterId);
            if (SubmissionStatus.APPROVED.equals(submission.getStatus())) {
                throw new BadRequestException("Approved submissions can only be deleted by admin");
            }
        }

        repository.delete(submission);
        documentService.removeSubmissionId(submission.getDocumentId(), StageStatus.FINAL, submission.getFinalId());
    }

    private void advanceProjectStage(String projectId, StageStatus stage) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        if (project.getStageStatus() != stage) {
            throw new BadRequestException("Project is not currently at stage " + stage);
        }
        project.setStageStatus(workflowService.nextStage(stage));
        if (stage == StageStatus.FINAL) {
            project.setStatus("completed");
        }
        projectRepository.save(project);
    }

    private void enforceVisibility(FinalSubmission submission, String requesterId) {
        if (requesterId == null || requesterId.isBlank()) {
            return;
        }
        String role = workflowService.resolveRole(requesterId);
        switch (role) {
            case "ADMIN" -> { }
            case "SUPERVISOR" -> workflowService.requireAssignedSupervisor(submission.getProjectId(), requesterId);
            case "STUDENT" -> workflowService.requireStudentTeamMember(submission.getProjectId(), requesterId);
            default -> { }
        }
    }

    private boolean matchesVisibility(FinalSubmission submission, String requesterId) {
        if (requesterId == null || requesterId.isBlank()) {
            return true;
        }
        String role = workflowService.resolveRole(requesterId);
        return switch (role) {
            case "ADMIN" -> true;
            case "SUPERVISOR" -> canSupervisorAccess(submission, requesterId);
            case "STUDENT" -> true;
            default -> false;
        };
    }

    private boolean canSupervisorAccess(FinalSubmission submission, String supervisorId) {
        try {
            workflowService.requireAssignedSupervisor(submission.getProjectId(), supervisorId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

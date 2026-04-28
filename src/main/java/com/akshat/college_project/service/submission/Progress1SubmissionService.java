package com.akshat.college_project.service.submission;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SupervisorSubmissionReviewRequest;
import com.akshat.college_project.dto.TeamReviewVote;
import com.akshat.college_project.dto.TeamSubmissionReviewRequest;
import com.akshat.college_project.entity.Progress1Submission;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.entity.enums.SubmissionStatus;
import com.akshat.college_project.repository.Progress1SubmissionRepository;
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
public class Progress1SubmissionService {

    private final Progress1SubmissionRepository repository;
    private final ReferenceValidator referenceValidator;
    private final DocumentService documentService;
    private final SubmissionWorkflowService workflowService;
    private final ProjectRepository projectRepository;

    public Progress1SubmissionService(
            Progress1SubmissionRepository repository,
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
    public Progress1Submission create(SubmissionCreateRequest request, String leaderId) {
        referenceValidator.requireDocument(request.documentId());
        var document = documentService.get(request.documentId());
        var project = workflowService.requireCurrentStageProject(document.getProjectId(), StageStatus.PROGRESS1);
        workflowService.requireLeader(project.getProjectId(), leaderId);
        workflowService.requireStageWindow(project.getProjectId(), StageStatus.PROGRESS1);

        List<Progress1Submission> existing = repository.findByDocumentId(request.documentId());
        Optional<Progress1Submission> latest = existing.stream()
                .sorted(Comparator.comparing(Progress1Submission::getVersionNo, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Progress1Submission::getUploadedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .reduce((first, second) -> second);

        Progress1Submission submission = new Progress1Submission();
        submission.setProgress1Id(IdGenerator.generate("pg1_"));
        submission.setProjectId(project.getProjectId());
        submission.setDocumentId(request.documentId());
        submission.setLeaderId(leaderId);
        submission.setVersionNo(latest.map(value -> value.getVersionNo() + 1).orElse(1));
        submission.setRevisionOf(latest.map(Progress1Submission::getProgress1Id).orElse(null));
        submission.setComment(request.comment());
        submission.setFileUrl(request.fileUrl());
        submission.setFileName(request.fileName());
        submission.setTeamReviewJson("[]");
        submission.setTeamReviewStatus("PENDING");
        submission.setVisibleToSupervisor(Boolean.FALSE);
        submission.setVisibleToAdmin(Boolean.FALSE);
        submission.setStatus(SubmissionStatus.PENDING);

        Progress1Submission saved = repository.save(submission);
        documentService.appendSubmissionId(saved.getDocumentId(), StageStatus.PROGRESS1, saved.getProgress1Id());
        return saved;
    }

    public Progress1Submission get(String submissionId, String requesterId) {
        Progress1Submission submission = repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress1 submission not found: " + submissionId));
        enforceVisibility(submission, requesterId);
        return submission;
    }

    public List<Progress1Submission> getAll(String requesterId) {
        return repository.findAll().stream().filter(submission -> matchesVisibility(submission, requesterId)).collect(Collectors.toList());
    }

    public List<Progress1Submission> getByDocument(String documentId, String requesterId) {
        try {
            referenceValidator.requireDocument(documentId);
        } catch (Exception ex) {
            return List.of();
        }
        List<Progress1Submission> submissions = repository.findByDocumentId(documentId);
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
    public Progress1Submission teamReview(String submissionId, TeamSubmissionReviewRequest request) {
        Progress1Submission submission = get(submissionId, request.reviewerId());
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
    public Progress1Submission supervisorReview(String submissionId, SupervisorSubmissionReviewRequest request) {
        Progress1Submission submission = get(submissionId, request.supervisorId());
        workflowService.requireAssignedSupervisor(submission.getProjectId(), request.supervisorId());

        submission.setComment(request.comment() != null ? request.comment() : submission.getComment());
        submission.setVisibleToSupervisor(Boolean.TRUE);
        if (request.approved()) {
            submission.setStatus(SubmissionStatus.APPROVED);
            submission.setVisibleToAdmin(Boolean.TRUE);
            advanceProjectStage(submission.getProjectId(), StageStatus.PROGRESS1);
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

        Progress1Submission submission = repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress1 submission not found: " + submissionId));

        boolean isAdmin = workflowService.isAdmin(requesterId);
        if (!isAdmin) {
            workflowService.requireLeader(submission.getProjectId(), requesterId);
            if (SubmissionStatus.APPROVED.equals(submission.getStatus())) {
                throw new BadRequestException("Approved submissions can only be deleted by admin");
            }
        }

        repository.delete(submission);
        documentService.removeSubmissionId(submission.getDocumentId(), StageStatus.PROGRESS1, submission.getProgress1Id());
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

    private void enforceVisibility(Progress1Submission submission, String requesterId) {
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

    private boolean matchesVisibility(Progress1Submission submission, String requesterId) {
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

    private boolean canSupervisorAccess(Progress1Submission submission, String supervisorId) {
        try {
            workflowService.requireAssignedSupervisor(submission.getProjectId(), supervisorId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

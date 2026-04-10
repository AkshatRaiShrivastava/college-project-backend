package com.akshat.college_project.service.submission;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SubmissionCreateRequest;
import com.akshat.college_project.dto.SupervisorSubmissionReviewRequest;
import com.akshat.college_project.dto.TeamReviewVote;
import com.akshat.college_project.dto.TeamSubmissionReviewRequest;
import com.akshat.college_project.entity.Document;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.SynopsisSubmission;
import com.akshat.college_project.entity.enums.CommentByRole;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.entity.enums.SubmissionStatus;
import com.akshat.college_project.repository.SynopsisSubmissionRepository;
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
public class SynopsisSubmissionService {

    private final SynopsisSubmissionRepository repository;
    private final ReferenceValidator referenceValidator;
    private final DocumentService documentService;
    private final SubmissionWorkflowService workflowService;
    private final ProjectRepository projectRepository;

    public SynopsisSubmissionService(
            SynopsisSubmissionRepository repository,
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
    public SynopsisSubmission create(SubmissionCreateRequest request, String leaderId) {
        referenceValidator.requireDocument(request.documentId());
        Document document = documentService.get(request.documentId());
        Project project = workflowService.requireCurrentStageProject(document.getProjectId(), StageStatus.SYNOPSIS);
        workflowService.requireLeader(project.getProjectId(), leaderId);
        workflowService.requireStageWindow(project.getProjectId(), StageStatus.SYNOPSIS);

        List<SynopsisSubmission> existing = repository.findByDocumentId(request.documentId());
        Optional<SynopsisSubmission> latest = existing.stream()
                .sorted(Comparator.comparing(SynopsisSubmission::getVersionNo, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(SynopsisSubmission::getUploadedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .reduce((first, second) -> second);

        SynopsisSubmission submission = new SynopsisSubmission();
        submission.setSynopsisId(IdGenerator.generate("syn_"));
        submission.setProjectId(project.getProjectId());
        submission.setDocumentId(request.documentId());
        submission.setLeaderId(leaderId);
        submission.setVersionNo(latest.map(value -> value.getVersionNo() + 1).orElse(1));
        submission.setRevisionOf(latest.map(SynopsisSubmission::getSynopsisId).orElse(null));
        submission.setComment(request.comment());
        submission.setFileUrl(request.fileUrl());
        submission.setFileName(request.fileName());
        submission.setTeamReviewJson("[]");
        submission.setTeamReviewStatus("PENDING");
        submission.setVisibleToSupervisor(Boolean.FALSE);
        submission.setVisibleToAdmin(Boolean.FALSE);
        submission.setStatus(SubmissionStatus.PENDING);

        SynopsisSubmission saved = repository.save(submission);
        documentService.appendSubmissionId(saved.getDocumentId(), StageStatus.SYNOPSIS, saved.getSynopsisId());
        return saved;
    }

    public SynopsisSubmission get(String submissionId, String requesterId) {
        SynopsisSubmission submission = repository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Synopsis submission not found: " + submissionId));
        enforceVisibility(submission, requesterId);
        return submission;
    }

    public List<SynopsisSubmission> getAll(String requesterId) {
        return repository.findAll().stream()
                .filter(submission -> matchesVisibility(submission, requesterId))
                .collect(Collectors.toList());
    }

    public List<SynopsisSubmission> getByDocument(String documentId, String requesterId) {
        referenceValidator.requireDocument(documentId);
        List<SynopsisSubmission> submissions = repository.findByDocumentId(documentId);
        if (requesterId == null || requesterId.isBlank()) {
            return submissions;
        }
        String role = workflowService.resolveRole(requesterId);
        return switch (role) {
            case "ADMIN" -> submissions.stream().filter(SynopsisSubmission::getVisibleToAdmin).collect(Collectors.toList());
            case "SUPERVISOR" -> submissions.stream().filter(SynopsisSubmission::getVisibleToSupervisor).collect(Collectors.toList());
            case "STUDENT" -> submissions;
            default -> submissions;
        };
    }

    @Transactional
    public SynopsisSubmission teamReview(String submissionId, TeamSubmissionReviewRequest request) {
        SynopsisSubmission submission = get(submissionId, request.reviewerId());
        workflowService.requireStudentTeamMember(submission.getProjectId(), request.reviewerId());

        List<TeamReviewVote> votes = workflowService.readVotes(submission.getTeamReviewJson());
        votes.removeIf(vote -> request.reviewerId().equals(vote.reviewerId()));
        votes.add(new TeamReviewVote(request.reviewerId(), request.approved(), request.comment(), Instant.now()));

        Set<String> requiredReviewers = new java.util.HashSet<>(workflowService.getTeamMemberIds(submission.getProjectId()));

        boolean anyReject = votes.stream().anyMatch(vote -> Boolean.FALSE.equals(vote.approved()));
        boolean allApproved = votes.stream().filter(vote -> Boolean.TRUE.equals(vote.approved()))
                .map(TeamReviewVote::reviewerId)
                .collect(Collectors.toSet())
                .containsAll(requiredReviewers);

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
    public SynopsisSubmission supervisorReview(String submissionId, SupervisorSubmissionReviewRequest request) {
        SynopsisSubmission submission = get(submissionId, request.supervisorId());
        workflowService.requireAssignedSupervisor(submission.getProjectId(), request.supervisorId());
        if (!"APPROVED".equals(submission.getTeamReviewStatus())) {
            throw new BadRequestException("Supervisor review is only available after full team approval");
        }

        submission.setComment(request.comment() != null ? request.comment() : submission.getComment());
        submission.setCommentByRole(CommentByRole.SUPERVISOR);
        submission.setCommentById(request.supervisorId());
        submission.setVisibleToSupervisor(Boolean.TRUE);

        if (request.approved()) {
            submission.setStatus(SubmissionStatus.APPROVED);
            submission.setVisibleToAdmin(Boolean.TRUE);
            advanceProjectStage(submission.getProjectId(), StageStatus.SYNOPSIS);
        } else {
            submission.setStatus(SubmissionStatus.REVISION);
            submission.setVisibleToAdmin(Boolean.FALSE);
        }

        return repository.save(submission);
    }

    public void delete(String submissionId) {
        SynopsisSubmission submission = get(submissionId, null);
        repository.delete(submission);
        documentService.removeSubmissionId(submission.getDocumentId(), StageStatus.SYNOPSIS, submission.getSynopsisId());
    }

    private void advanceProjectStage(String projectId, StageStatus stage) {
        com.akshat.college_project.entity.Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        if (project.getStageStatus() != stage) {
            throw new BadRequestException("Project is not currently at stage " + stage);
        }
        project.setStageStatus(workflowService.nextStage(stage));
        if (stage == StageStatus.FINAL) {
            project.setStatus("completed");
        }
        projectRepository.save(project);
    }

    private void enforceVisibility(SynopsisSubmission submission, String requesterId) {
        if (requesterId == null || requesterId.isBlank()) {
            return;
        }
        String role = workflowService.resolveRole(requesterId);
        switch (role) {
            case "ADMIN" -> {
                if (!Boolean.TRUE.equals(submission.getVisibleToAdmin())) {
                    throw new BadRequestException("This submission is not visible to admin yet");
                }
            }
            case "SUPERVISOR" -> {
                if (!Boolean.TRUE.equals(submission.getVisibleToSupervisor())) {
                    throw new BadRequestException("This submission is not visible to supervisor yet");
                }
            }
            case "STUDENT" -> workflowService.requireStudentTeamMember(submission.getProjectId(), requesterId);
            default -> { }
        }
    }

    private boolean matchesVisibility(SynopsisSubmission submission, String requesterId) {
        if (requesterId == null || requesterId.isBlank()) {
            return true;
        }
        String role = workflowService.resolveRole(requesterId);
        return switch (role) {
            case "ADMIN" -> Boolean.TRUE.equals(submission.getVisibleToAdmin());
            case "SUPERVISOR" -> Boolean.TRUE.equals(submission.getVisibleToSupervisor());
            case "STUDENT" -> true;
            default -> false;
        };
    }
}

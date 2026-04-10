package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.TeamReviewVote;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.Team;
import com.akshat.college_project.entity.Timeline;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.repository.TeamRepository;
import com.akshat.college_project.repository.TimelineRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SubmissionWorkflowService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TimelineRepository timelineRepository;
    private final ReferenceValidator referenceValidator;
    private final ObjectMapper objectMapper;

    public SubmissionWorkflowService(
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            TimelineRepository timelineRepository,
            ReferenceValidator referenceValidator,
            ObjectMapper objectMapper
    ) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.timelineRepository = timelineRepository;
        this.referenceValidator = referenceValidator;
        this.objectMapper = objectMapper;
    }

    public Project requireCurrentStageProject(String projectId, StageStatus stage) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        if (project.getStageStatus() != stage) {
            throw new BadRequestException("Project is currently on stage " + project.getStageStatus() + " and cannot accept " + stage + " submissions");
        }
        return project;
    }

    public Team requireLeader(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        Team team = teamRepository.findById(project.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + project.getTeamId()));
        if (!team.getLeaderId().equals(userId)) {
            throw new BadRequestException("Only the team leader can upload or revise submissions");
        }
        return team;
    }

    public void requireStudentTeamMember(String projectId, String studentId) {
        Team team = getTeamForProject(projectId);
        if (!team.getLeaderId().equals(studentId) && !getTeamMemberIds(team).contains(studentId)) {
            throw new BadRequestException("Only the project team can review submissions");
        }
    }

    public void requireAssignedSupervisor(String projectId, String supervisorId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        if (project.getSupervisorId() == null || !project.getSupervisorId().equals(supervisorId)) {
            throw new BadRequestException("Only the assigned supervisor can review this submission");
        }
    }

    public void requireStageWindow(String projectId, StageStatus stage) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        Timeline timeline = timelineRepository.findByFormId(project.getFormId()).orElse(null);
        if (timeline == null) {
            return;
        }
        Instant now = Instant.now();
        Instant deadline = switch (stage) {
            case SYNOPSIS -> timeline.getSynopsisDate();
            case PROGRESS1 -> timeline.getProgress1Date();
            case PROGRESS2 -> timeline.getProgress2Date();
            case FINAL -> timeline.getFinalSubmissionDate();
        };
        if (deadline != null && now.isAfter(deadline)) {
            throw new BadRequestException("The submission window for " + stage + " has closed");
        }
    }

    public StageStatus nextStage(StageStatus stage) {
        return switch (stage) {
            case SYNOPSIS -> StageStatus.PROGRESS1;
            case PROGRESS1 -> StageStatus.PROGRESS2;
            case PROGRESS2 -> StageStatus.FINAL;
            case FINAL -> StageStatus.FINAL;
        };
    }

    public List<String> getTeamMemberIds(String projectId) {
        return getTeamMemberIds(getTeamForProject(projectId));
    }

    public boolean isAdmin(String userId) {
        return userId != null && !userId.isBlank() && referenceValidator != null && existsAdmin(userId);
    }

    public boolean isSupervisor(String userId) {
        return userId != null && !userId.isBlank() && existsSupervisor(userId);
    }

    public boolean isStudent(String userId) {
        return userId != null && !userId.isBlank() && existsStudent(userId);
    }

    public String resolveRole(String userId) {
        if (isAdmin(userId)) {
            return "ADMIN";
        }
        if (isSupervisor(userId)) {
            return "SUPERVISOR";
        }
        if (isStudent(userId)) {
            return "STUDENT";
        }
        return "UNKNOWN";
    }

    public List<TeamReviewVote> readVotes(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<TeamReviewVote>>() {});
        } catch (Exception ex) {
            throw new BadRequestException("Stored team review state is invalid");
        }
    }

    public String writeVotes(List<TeamReviewVote> votes) {
        try {
            return objectMapper.writeValueAsString(votes == null ? Collections.emptyList() : votes);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize team review state", ex);
        }
    }

    private Team getTeamForProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        return teamRepository.findById(project.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + project.getTeamId()));
    }

    private List<String> getTeamMemberIds(Team team) {
        try {
            if (team.getTeamMemberArray() == null || team.getTeamMemberArray().isBlank()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(team.getTeamMemberArray(), new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            throw new BadRequestException("Stored team member list is invalid");
        }
    }

    private boolean existsAdmin(String userId) {
        try {
            referenceValidator.requireAdmin(userId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean existsSupervisor(String userId) {
        try {
            referenceValidator.requireSupervisor(userId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean existsStudent(String userId) {
        try {
            referenceValidator.requireStudent(userId);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
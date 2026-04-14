package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.LeaderboardCreateRequest;
import com.akshat.college_project.dto.LeaderboardUpdateRequest;
import com.akshat.college_project.entity.Leaderboard;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.repository.LeaderboardRepository;
import com.akshat.college_project.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final ProjectRepository projectRepository;
    private final ReferenceValidator referenceValidator;

    public LeaderboardService(
            LeaderboardRepository leaderboardRepository,
            ProjectRepository projectRepository,
            ReferenceValidator referenceValidator
    ) {
        this.leaderboardRepository = leaderboardRepository;
        this.projectRepository = projectRepository;
        this.referenceValidator = referenceValidator;
    }

    public Leaderboard create(LeaderboardCreateRequest request) {
        referenceValidator.requireProject(request.projectId());
        referenceValidator.requireTeam(request.teamId());
        if (request.supervisorId() != null) {
            referenceValidator.requireSupervisor(request.supervisorId());
        }

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        if (!project.getTeamId().equals(request.teamId())) {
            throw new BadRequestException("Team does not match project mapping");
        }

        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setProjectId(request.projectId());
        leaderboard.setTeamId(request.teamId());
        leaderboard.setSupervisorId(request.supervisorId());
        leaderboard.setRank(request.rank());
        leaderboard.setScore(request.score());
        leaderboard.setUpdateStatus(request.updateStatus());
        return leaderboardRepository.save(leaderboard);
    }

    public Leaderboard get(String projectId) {
        return leaderboardRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Leaderboard not found for project: " + projectId));
    }

    public List<Leaderboard> getAll() {
        return leaderboardRepository.findAllByOrderByRankAsc();
    }

    public Leaderboard update(String projectId, LeaderboardUpdateRequest request) {
        Leaderboard leaderboard = get(projectId);

        if (request.teamId() != null) {
            referenceValidator.requireTeam(request.teamId());
            leaderboard.setTeamId(request.teamId());
        }

        if (request.supervisorId() != null) {
            referenceValidator.requireSupervisor(request.supervisorId());
            leaderboard.setSupervisorId(request.supervisorId());
        }

        if (request.rank() != null) {
            leaderboard.setRank(request.rank());
        }
        if (request.score() != null) {
            leaderboard.setScore(request.score());
        }
        if (request.updateStatus() != null) {
            leaderboard.setUpdateStatus(request.updateStatus());
        }

        return leaderboardRepository.save(leaderboard);
    }

    public void delete(String projectId) {
        Leaderboard leaderboard = get(projectId);
        leaderboardRepository.delete(leaderboard);
    }
}

package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.JsonArrayCodec;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.TeamCreateRequest;
import com.akshat.college_project.dto.TeamUpdateRequest;
import com.akshat.college_project.entity.Team;
import com.akshat.college_project.entity.TeamMembers;
import com.akshat.college_project.repository.TeamMembersRepository;
import com.akshat.college_project.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;
    private final ReferenceValidator referenceValidator;
    private final JsonArrayCodec jsonArrayCodec;

    public TeamService(
            TeamRepository teamRepository,
            TeamMembersRepository teamMembersRepository,
            ReferenceValidator referenceValidator,
            JsonArrayCodec jsonArrayCodec
    ) {
        this.teamRepository = teamRepository;
        this.teamMembersRepository = teamMembersRepository;
        this.referenceValidator = referenceValidator;
        this.jsonArrayCodec = jsonArrayCodec;
    }

    @Transactional
    public Team create(TeamCreateRequest request) {
        referenceValidator.requireStudent(request.leaderId());
        if (teamRepository.existsByLeaderId(request.leaderId())) {
            throw new BadRequestException("Leader already owns a team");
        }

        List<String> memberIds = normalizeMembers(request.leaderId(), request.teamMemberIds());
        validateStudents(memberIds);

        Team team = new Team();
        team.setTeamId(IdGenerator.generate("tem_"));
        team.setLeaderId(request.leaderId());
        team.setTeamMemberArray(jsonArrayCodec.toJson(memberIds));
        team.setTeamLength(memberIds.size());
        team.setTeamCompleteStatus(Boolean.TRUE.equals(request.teamCompleteStatus()));
        Team saved = teamRepository.save(team);

        TeamMembers teamMembers = new TeamMembers();
        teamMembers.setTeamId(saved.getTeamId());
        teamMembers.setJoinMemberArray(saved.getTeamMemberArray());
        teamMembers.setNotJoinMemberArray("[]");
        teamMembersRepository.save(teamMembers);

        return saved;
    }

    public Team get(String teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));
    }

    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    @Transactional
    public Team update(String teamId, TeamUpdateRequest request) {
        Team team = get(teamId);

        if (request.projectId() != null) {
            referenceValidator.requireProject(request.projectId());
            team.setProjectId(request.projectId());
        }

        if (request.teamMemberIds() != null) {
            List<String> memberIds = normalizeMembers(team.getLeaderId(), request.teamMemberIds());
            validateStudents(memberIds);
            team.setTeamMemberArray(jsonArrayCodec.toJson(memberIds));
            team.setTeamLength(memberIds.size());

            TeamMembers teamMembers = teamMembersRepository.findById(teamId)
                    .orElseGet(() -> new TeamMembers(teamId, "[]", "[]"));
            teamMembers.setJoinMemberArray(team.getTeamMemberArray());
            teamMembersRepository.save(teamMembers);
        }

        if (request.teamCompleteStatus() != null) {
            team.setTeamCompleteStatus(request.teamCompleteStatus());
        }

        return teamRepository.save(team);
    }

    @Transactional
    public void delete(String teamId) {
        Team team = get(teamId);
        teamMembersRepository.findById(teamId).ifPresent(teamMembersRepository::delete);
        teamRepository.delete(team);
    }

    private List<String> normalizeMembers(String leaderId, List<String> incomingMembers) {
        List<String> members = new ArrayList<>();
        if (incomingMembers != null) {
            members.addAll(incomingMembers);
        }
        members.add(leaderId);
        return new ArrayList<>(new LinkedHashSet<>(members));
    }

    private void validateStudents(List<String> studentIds) {
        if (studentIds.isEmpty()) {
            throw new BadRequestException("Team must have at least one member");
        }
        for (String studentId : studentIds) {
            referenceValidator.requireStudent(studentId);
        }
    }
}

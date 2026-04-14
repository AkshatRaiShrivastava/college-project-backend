package com.akshat.college_project.service;

import com.akshat.college_project.common.JsonArrayCodec;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.TeamMembersUpsertRequest;
import com.akshat.college_project.entity.Team;
import com.akshat.college_project.entity.TeamMembers;
import com.akshat.college_project.repository.TeamMembersRepository;
import com.akshat.college_project.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamMembersService {

    private final TeamMembersRepository teamMembersRepository;
    private final TeamRepository teamRepository;
    private final ReferenceValidator referenceValidator;
    private final JsonArrayCodec jsonArrayCodec;

    public TeamMembersService(
            TeamMembersRepository teamMembersRepository,
            TeamRepository teamRepository,
            ReferenceValidator referenceValidator,
            JsonArrayCodec jsonArrayCodec
    ) {
        this.teamMembersRepository = teamMembersRepository;
        this.teamRepository = teamRepository;
        this.referenceValidator = referenceValidator;
        this.jsonArrayCodec = jsonArrayCodec;
    }

    public TeamMembers get(String teamId) {
        return teamMembersRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team members not found for team: " + teamId));
    }

    public List<TeamMembers> getAll() {
        return teamMembersRepository.findAll();
    }

    @Transactional
    public TeamMembers upsert(String teamId, TeamMembersUpsertRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        if (request.joinMemberIds() != null) {
            for (String memberId : request.joinMemberIds()) {
                referenceValidator.requireStudent(memberId);
            }
        }
        if (request.notJoinMemberIds() != null) {
            for (String memberId : request.notJoinMemberIds()) {
                referenceValidator.requireStudent(memberId);
            }
        }

        if (request.rejectedMemberIds() != null) {
            for (String memberId : request.rejectedMemberIds()) {
                referenceValidator.requireStudent(memberId);
            }
        }

        TeamMembers teamMembers = teamMembersRepository.findById(teamId)
                .orElseGet(() -> new TeamMembers(teamId, "[]", "[]", "[]"));

        if (request.joinMemberIds() != null) {
            String joinedJson = jsonArrayCodec.toJson(request.joinMemberIds());
            teamMembers.setJoinMemberArray(joinedJson);
            team.setTeamMemberArray(joinedJson);
            team.setTeamLength(request.joinMemberIds().size());
        }

        if (request.notJoinMemberIds() != null) {
            teamMembers.setNotJoinMemberArray(jsonArrayCodec.toJson(request.notJoinMemberIds()));
        }

        if (request.rejectedMemberIds() != null) {
            teamMembers.setRejectedMemberArray(jsonArrayCodec.toJson(request.rejectedMemberIds()));
        }

        teamRepository.save(team);
        return teamMembersRepository.save(teamMembers);
    }
}

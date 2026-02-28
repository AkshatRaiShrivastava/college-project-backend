package com.akshat.college_project.controller;

import com.akshat.college_project.dto.TeamMembersUpsertRequest;
import com.akshat.college_project.entity.TeamMembers;
import com.akshat.college_project.service.TeamMembersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
public class TeamMembersController {

    private final TeamMembersService teamMembersService;

    public TeamMembersController(TeamMembersService teamMembersService) {
        this.teamMembersService = teamMembersService;
    }

    @GetMapping("/{teamId}")
    public TeamMembers get(@PathVariable String teamId) {
        return teamMembersService.get(teamId);
    }

    @GetMapping
    public List<TeamMembers> getAll() {
        return teamMembersService.getAll();
    }

    @PutMapping("/{teamId}")
    public TeamMembers upsert(@PathVariable String teamId, @RequestBody TeamMembersUpsertRequest request) {
        return teamMembersService.upsert(teamId, request);
    }
}

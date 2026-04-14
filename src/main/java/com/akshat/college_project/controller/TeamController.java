package com.akshat.college_project.controller;

import com.akshat.college_project.dto.TeamCreateRequest;
import com.akshat.college_project.dto.TeamUpdateRequest;
import com.akshat.college_project.entity.Team;
import com.akshat.college_project.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<Team> create(@Valid @RequestBody TeamCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(request));
    }

    @GetMapping("/{teamId}")
    public Team get(@PathVariable String teamId) {
        return teamService.get(teamId);
    }

    @GetMapping
    public List<Team> getAll() {
        return teamService.getAll();
    }

    @PutMapping("/{teamId}")
    public Team update(@PathVariable String teamId, @RequestBody TeamUpdateRequest request) {
        return teamService.update(teamId, request);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> delete(@PathVariable String teamId) {
        teamService.delete(teamId);
        return ResponseEntity.noContent().build();
    }
}

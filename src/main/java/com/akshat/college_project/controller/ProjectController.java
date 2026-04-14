package com.akshat.college_project.controller;

import com.akshat.college_project.dto.ProjectCreateRequest;
import com.akshat.college_project.dto.ProjectUpdateRequest;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.service.ProjectService;
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
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<Project> create(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }

    @GetMapping("/{projectId}")
    public Project get(@PathVariable String projectId) {
        return projectService.get(projectId);
    }

    @GetMapping("/by-team/{teamId}")
    public Project getByTeam(@PathVariable String teamId) {
        return projectService.getByTeamId(teamId);
    }

    @GetMapping
    public List<Project> getAll() {
        return projectService.getAll();
    }

    @PutMapping("/{projectId}")
    public Project update(@PathVariable String projectId, @RequestBody ProjectUpdateRequest request) {
        return projectService.update(projectId, request);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable String projectId) {
        projectService.delete(projectId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/assign-supervisor")
    public Project assignSupervisor(@PathVariable String projectId, @Valid @RequestBody com.akshat.college_project.dto.SupervisorAssignRequest request) {
        return projectService.assignSupervisor(projectId, request);
    }

    @GetMapping("/{projectId}/supervisor-history")
    public List<com.akshat.college_project.entity.SupervisorHistory> getSupervisorHistory(@PathVariable String projectId) {
        return projectService.getSupervisorHistory(projectId);
    }
}

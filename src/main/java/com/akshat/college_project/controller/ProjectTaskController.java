package com.akshat.college_project.controller;

import com.akshat.college_project.entity.ProjectTask;
import com.akshat.college_project.service.ProjectTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    public ProjectTaskController(ProjectTaskService projectTaskService) {
        this.projectTaskService = projectTaskService;
    }

    @PostMapping
    public ResponseEntity<ProjectTask> create(@RequestBody ProjectTask task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectTaskService.create(task));
    }

    @PutMapping("/{taskId}")
    public ProjectTask update(@PathVariable String taskId, @RequestBody ProjectTask request) {
        return projectTaskService.update(taskId, request);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable String taskId) {
        projectTaskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    public List<ProjectTask> getTasksByProject(@PathVariable String projectId) {
        return projectTaskService.getTasksByProject(projectId);
    }

    @GetMapping("/project/{projectId}/stage/{stage}")
    public List<ProjectTask> getTasksByStage(@PathVariable String projectId, @PathVariable String stage) {
        return projectTaskService.getTasksByProjectAndStage(projectId, stage);
    }
}

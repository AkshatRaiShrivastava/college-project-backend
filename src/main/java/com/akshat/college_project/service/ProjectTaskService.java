package com.akshat.college_project.service;

import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.entity.ProjectTask;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.ProjectTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {

    private final ProjectTaskRepository repository;

    public ProjectTaskService(ProjectTaskRepository repository) {
        this.repository = repository;
    }

    public ProjectTask create(ProjectTask request) {
        request.setTaskId(IdGenerator.generate("tsk_"));
        return repository.save(request);
    }

    public ProjectTask update(String taskId, ProjectTask request) {
        ProjectTask existing = repository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        
        if (request.getTitle() != null) existing.setTitle(request.getTitle());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getAssigneeId() != null) existing.setAssigneeId(request.getAssigneeId());
        if (request.getPriority() != null) existing.setPriority(request.getPriority());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
        if (request.getDueDate() != null) existing.setDueDate(request.getDueDate());
        
        return repository.save(existing);
    }

    public void delete(String taskId) {
        repository.deleteById(taskId);
    }

    public List<ProjectTask> getTasksByProject(String projectId) {
        return repository.findByProjectId(projectId);
    }

    public List<ProjectTask> getTasksByProjectAndStage(String projectId, String stageStr) {
        StageStatus stage = StageStatus.valueOf(stageStr.toUpperCase());
        return repository.findByProjectIdAndStageStatus(projectId, stage);
    }

    public List<ProjectTask> getTasksByAssignee(String assigneeId) {
        return repository.findByAssigneeId(assigneeId);
    }
}

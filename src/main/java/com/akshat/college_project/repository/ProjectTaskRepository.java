package com.akshat.college_project.repository;

import com.akshat.college_project.entity.ProjectTask;
import com.akshat.college_project.entity.enums.StageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, String> {
    List<ProjectTask> findByProjectId(String projectId);
    List<ProjectTask> findByProjectIdAndStageStatus(String projectId, StageStatus stageStatus);
    List<ProjectTask> findByAssigneeId(String assigneeId);
}

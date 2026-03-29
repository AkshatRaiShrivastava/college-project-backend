package com.akshat.college_project.entity;

import com.akshat.college_project.entity.enums.StageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "project_tasks")
public class ProjectTask {

    @Id
    @Column(name = "task_id", length = 30)
    private String taskId;

    @Column(name = "project_id", nullable = false, length = 30)
    private String projectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_status", nullable = false, length = 50)
    private StageStatus stageStatus;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "assignee_id", length = 30)
    private String assigneeId; // The student assigned to the task

    @Column(name = "priority", length = 20)
    private String priority; // HIGH, MEDIUM, LOW

    @Column(name = "status", length = 30)
    private String status; // TODO, IN_PROGRESS, REVIEW, DONE

    @Column(name = "due_date")
    private Instant dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = "TODO";
        }
        if (priority == null) {
            priority = "MEDIUM";
        }
    }
}

package com.akshat.college_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "supervisor_history")
public class SupervisorHistory {

    @Id
    @Column(name = "id", length = 30)
    private String id;

    @Column(name = "project_id", nullable = false, length = 30)
    private String projectId;

    @Column(name = "old_supervisor_id", length = 30)
    private String oldSupervisorId;

    @Column(name = "new_supervisor_id", nullable = false, length = 30)
    private String newSupervisorId;

    @Column(name = "changed_by", nullable = false, length = 30)
    private String changedBy; // admin_id

    @Column(name = "reason", columnDefinition = "text")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

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
@Table(name = "project")
public class Project {

    @Id
    @Column(name = "project_id", length = 30)
    private String projectId;

    @Column(name = "team_id", nullable = false, length = 30)
    private String teamId;

    @Column(name = "form_id", nullable = false, length = 30)
    private String formId;

    @Column(name = "supervisor_id", length = 30)
    private String supervisorId;

    @Column(name = "project_tittle", nullable = false, length = 255)
    private String projectTitle;

    @Column(name = "project_description", nullable = false, columnDefinition = "text")
    private String projectDescription;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_status", nullable = false, length = 50)
    private StageStatus stageStatus;

    @Column(name = "synopsis_score", length = 50)
    private String synopsisScore;

    @Column(name = "progress1_score", length = 50)
    private String progress1Score;

    @Column(name = "progress2_score", length = 50)
    private String progress2Score;

    @Column(name = "final_score", length = 50)
    private String finalScore;

    @Column(name = "admin_final_score", length = 50)
    private String adminFinalScore;

    @Column(name = "stages_score_out_of", length = 50)
    private String stagesScoreOutOf;

    @Column(name = "admin_finalscore_out_of", length = 50)
    private String adminFinalscoreOutOf;

    @Column(name = "old_supervisor", length = 30)
    private String oldSupervisor;

    @Column(name = "supervisor_assigned_by", length = 30)
    private String supervisorAssignedBy;

    @Column(name = "document_id", length = 30)
    private String documentId;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = "pending";
        }
        if (stageStatus == null) {
            stageStatus = StageStatus.SYNOPSIS;
        }
    }

    @jakarta.persistence.Transient
    public Double getCalculatedSupervisorAverage() {
        int count = 0;
        double sum = 0;
        if (synopsisScore != null && !synopsisScore.isEmpty()) { sum += Double.parseDouble(synopsisScore); count++; }
        if (progress1Score != null && !progress1Score.isEmpty()) { sum += Double.parseDouble(progress1Score); count++; }
        if (progress2Score != null && !progress2Score.isEmpty()) { sum += Double.parseDouble(progress2Score); count++; }
        if (finalScore != null && !finalScore.isEmpty()) { sum += Double.parseDouble(finalScore); count++; }
        return count == 0 ? 0.0 : sum / count;
    }

    @jakarta.persistence.Transient
    public Double getCalculatedFinalScore() {
        double adminScore = (adminFinalScore != null && !adminFinalScore.isEmpty()) ? Double.parseDouble(adminFinalScore) : 0.0;
        return getCalculatedSupervisorAverage() + adminScore;
    }
}

package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.StageStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
        @NotBlank @Size(max = 30) String teamId,
        @NotBlank @Size(max = 30) String formId,
        @Size(max = 30) String supervisorId,
        @NotBlank @Size(max = 255) String projectTitle,
        @NotBlank String projectDescription,
        @Size(max = 50) String status,
        StageStatus stageStatus,
        String synopsisScore,
        String progress1Score,
        String progress2Score,
        String finalScore,
        String adminFinalScore,
        String stagesScoreOutOf,
        String adminFinalscoreOutOf,
        String oldSupervisor,
        String supervisorAssignedBy,
        String documentId
) {
}

package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.StageStatus;

public record ProjectUpdateRequest(
        String teamId,
        String formId,
        String supervisorId,
        String projectTitle,
        String projectDescription,
        String status,
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

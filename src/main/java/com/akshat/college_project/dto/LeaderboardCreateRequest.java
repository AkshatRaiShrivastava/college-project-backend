package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeaderboardCreateRequest(
        @NotBlank @Size(max = 30) String teamId,
        @Size(max = 30) String supervisorId,
        @NotBlank @Size(max = 30) String projectId,
        Integer rank,
        Integer score,
        @NotNull Boolean updateStatus
) {
}

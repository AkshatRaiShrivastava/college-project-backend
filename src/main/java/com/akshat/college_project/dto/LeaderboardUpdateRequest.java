package com.akshat.college_project.dto;

public record LeaderboardUpdateRequest(
        String teamId,
        String supervisorId,
        Integer rank,
        Integer score,
        Boolean updateStatus
) {
}

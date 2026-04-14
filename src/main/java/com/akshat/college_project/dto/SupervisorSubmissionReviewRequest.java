package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SupervisorSubmissionReviewRequest(
        @NotBlank String supervisorId,
        @NotNull Boolean approved,
        String comment
) {
}
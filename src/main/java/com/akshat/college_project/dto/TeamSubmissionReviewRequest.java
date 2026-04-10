package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamSubmissionReviewRequest(
        @NotBlank String reviewerId,
        @NotNull Boolean approved,
        String comment
) {
}
package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.CommentByRole;
import com.akshat.college_project.entity.enums.SubmissionStatus;
import jakarta.validation.constraints.NotNull;

public record SubmissionReviewRequest(
        String comment,
        @NotNull SubmissionStatus status,
        CommentByRole commentByRole,
        String commentById
) {
}

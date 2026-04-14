package com.akshat.college_project.dto;

import java.time.Instant;

public record TeamReviewVote(
        String reviewerId,
        Boolean approved,
        String comment,
        Instant reviewedAt
) {
}
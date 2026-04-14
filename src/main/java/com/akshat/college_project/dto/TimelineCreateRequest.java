package com.akshat.college_project.dto;

import java.time.Instant;

public record TimelineCreateRequest(
        String formId,
        Instant synopsisDate,
        Instant progress1Date,
        Instant progress2Date,
        Instant finalSubmissionDate
) {}

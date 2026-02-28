package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmissionCreateRequest(
        @NotBlank @Size(max = 30) String documentId,
        String comment
) {
}

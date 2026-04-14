package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FormCreateRequest(
        @NotBlank @Size(max = 100) String accessBranch,
        @NotBlank @Size(max = 50) String accessBatch,
        @NotBlank String jsonOfFields,
        @NotBlank @Size(max = 30) String createdBy
) {
}

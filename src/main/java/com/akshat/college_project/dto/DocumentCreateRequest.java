package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentCreateRequest(
        @NotBlank @Size(max = 30) String projectId
) {
}

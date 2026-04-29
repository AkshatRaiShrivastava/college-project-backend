package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;

public record TemplateLinkRequest(
        @NotBlank(message = "Form ID is required") String formId,
        @NotBlank(message = "Stage ID is required") String stageId,
        @NotBlank(message = "Name is required") String name,
        String description,
        @NotBlank(message = "File URL is required") String fileUrl
) {}

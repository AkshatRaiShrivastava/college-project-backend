package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;

public record FormAttachmentLinkRequest(
        @NotBlank String fileName,
        @NotBlank String fileUrl,
        String uploadedBy
) {
}

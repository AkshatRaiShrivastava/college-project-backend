package com.akshat.college_project.dto;

import java.time.Instant;

public record FormAttachment(
        String attachmentId,
        String fileName,
        String fileUrl,
        String uploadedBy,
        Instant uploadedAt,
        String source
) {
}
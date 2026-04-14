package com.akshat.college_project.dto;

public record FormUpdateRequest(
        String accessBranch,
        String accessBatch,
        String jsonOfFields,
        String createdBy
) {
}

package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TeamCreateRequest(
        @NotBlank @Size(max = 30) String leaderId,
        List<String> teamMemberIds,
        Boolean teamCompleteStatus
) {
}

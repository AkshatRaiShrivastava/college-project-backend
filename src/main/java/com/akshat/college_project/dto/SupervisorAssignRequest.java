package com.akshat.college_project.dto;

import jakarta.validation.constraints.NotBlank;

public record SupervisorAssignRequest(
        @NotBlank(message = "Supervisor ID is required")
        String supervisorId,
        
        @NotBlank(message = "Admin ID is required")
        String adminId,
        
        String reason
) {
}

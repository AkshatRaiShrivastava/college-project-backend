package com.akshat.college_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupervisorRequestCreateRequest(
        @NotBlank @Size(max = 100) String supervisorName,
        @NotBlank @Size(max = 50) String department,
        @NotBlank @Email @Size(max = 150) String mail,
        @NotBlank @Size(max = 20) String phoneNumber,
        @NotBlank @Size(max = 255) String password
) {
}

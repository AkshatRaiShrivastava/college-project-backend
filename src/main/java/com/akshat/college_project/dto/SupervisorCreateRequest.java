package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.SupervisorEnrollStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupervisorCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 150) String mail,
        @NotBlank @Size(max = 255) String password,
        @NotBlank @Size(max = 50) String branch,
        @NotBlank @Size(max = 10) String otpCode,
        SupervisorEnrollStatus enrollStatus
) {
}

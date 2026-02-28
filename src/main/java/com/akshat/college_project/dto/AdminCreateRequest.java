package com.akshat.college_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 150) String mail,
        @NotBlank @Size(max = 255) String password,
        @NotBlank @Size(max = 50) String department
) {
}

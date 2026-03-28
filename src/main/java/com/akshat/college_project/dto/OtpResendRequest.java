package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpResendRequest(
        @NotBlank @Email String email,
        @NotNull AccountType accountType
) {
}

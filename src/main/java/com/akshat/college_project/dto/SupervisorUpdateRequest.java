package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.SupervisorEnrollStatus;

public record SupervisorUpdateRequest(
        String name,
        String mail,
        String password,
        String branch,
        SupervisorEnrollStatus enrollStatus
) {
}

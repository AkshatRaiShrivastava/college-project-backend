package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.RequestStatus;

public record SupervisorRequestUpdateRequest(
        String supervisorName,
        String department,
        String mail,
        String phoneNumber,
        RequestStatus status
) {
}

package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.StudentEnrollStatus;

public record StudentUpdateRequest(
        String name,
        String mail,
        String password,
        String rollNo,
        String branch,
        String batch,
        StudentEnrollStatus enrollStatus
) {
}

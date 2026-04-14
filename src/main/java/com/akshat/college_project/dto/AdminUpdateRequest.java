package com.akshat.college_project.dto;

public record AdminUpdateRequest(
        String name,
        String mail,
        String password,
        String department
) {
}

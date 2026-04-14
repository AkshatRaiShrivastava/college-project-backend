package com.akshat.college_project.dto;

public record MeetingConfigCreateRequest(
        String formId,
        Integer synopsisRequired,
        Integer progress1Required,
        Integer progress2Required,
        Integer finalRequired
) {}

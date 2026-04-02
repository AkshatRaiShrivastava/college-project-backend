package com.akshat.college_project.dto;

import java.util.List;

public record MeetingExecuteRequest(
        String conclusionNotes,
        List<String> presentStudentIds
) {}

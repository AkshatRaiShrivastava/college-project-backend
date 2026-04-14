package com.akshat.college_project.dto;

import com.akshat.college_project.entity.enums.MeetingMode;
import com.akshat.college_project.entity.enums.StageStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record MeetingCreateRequest(
        String projectId,
        String supervisorId,
        StageStatus stage,
        LocalDate meetingDate,
        LocalTime meetingTime,
        MeetingMode mode,
        String locationOrLink
) {}

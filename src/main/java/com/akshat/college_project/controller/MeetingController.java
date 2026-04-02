package com.akshat.college_project.controller;

import com.akshat.college_project.dto.MeetingCreateRequest;
import com.akshat.college_project.dto.MeetingExecuteRequest;
import com.akshat.college_project.entity.Meeting;
import com.akshat.college_project.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @PostMapping
    public ResponseEntity<Meeting> scheduleMeeting(@RequestBody MeetingCreateRequest request) {
        return ResponseEntity.ok(meetingService.scheduleMeeting(request));
    }

    @PostMapping("/{meetingId}/execute")
    public ResponseEntity<Meeting> executeMeeting(@PathVariable String meetingId, @RequestBody MeetingExecuteRequest request) {
        return ResponseEntity.ok(meetingService.executeMeeting(meetingId, request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Meeting>> getMeetingsByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(meetingService.getMeetingsByProject(projectId));
    }

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<List<Meeting>> getMeetingsBySupervisor(@PathVariable String supervisorId) {
        return ResponseEntity.ok(meetingService.getMeetingsBySupervisor(supervisorId));
    }
}

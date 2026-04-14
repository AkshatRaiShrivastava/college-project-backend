package com.akshat.college_project.controller;

import com.akshat.college_project.dto.MeetingConfigCreateRequest;
import com.akshat.college_project.dto.TimelineCreateRequest;
import com.akshat.college_project.entity.MeetingConfig;
import com.akshat.college_project.entity.Timeline;
import com.akshat.college_project.service.AdminConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/config")
public class AdminConfigController {

    private final AdminConfigService adminConfigService;

    public AdminConfigController(AdminConfigService adminConfigService) {
        this.adminConfigService = adminConfigService;
    }

    @PostMapping("/timeline")
    public ResponseEntity<Timeline> saveTimeline(@RequestBody TimelineCreateRequest request) {
        return ResponseEntity.ok(adminConfigService.saveTimeline(request));
    }

    @PostMapping("/meeting-config")
    public ResponseEntity<MeetingConfig> saveMeetingConfig(@RequestBody MeetingConfigCreateRequest request) {
        return ResponseEntity.ok(adminConfigService.saveMeetingConfig(request));
    }

    @GetMapping("/timeline/{formId}")
    public ResponseEntity<Timeline> getTimeline(@PathVariable String formId) {
        return ResponseEntity.ok(adminConfigService.getTimelineByFormId(formId));
    }

    @GetMapping("/meeting-config/{formId}")
    public ResponseEntity<MeetingConfig> getMeetingConfig(@PathVariable String formId) {
        return ResponseEntity.ok(adminConfigService.getMeetingConfigByFormId(formId));
    }
}

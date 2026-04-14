package com.akshat.college_project.service;

import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.dto.MeetingConfigCreateRequest;
import com.akshat.college_project.dto.TimelineCreateRequest;
import com.akshat.college_project.entity.MeetingConfig;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.Team;
import com.akshat.college_project.entity.Timeline;
import com.akshat.college_project.repository.MeetingConfigRepository;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.repository.TeamRepository;
import com.akshat.college_project.repository.TimelineRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AdminConfigService {

    private final TimelineRepository timelineRepository;
    private final MeetingConfigRepository meetingConfigRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public AdminConfigService(TimelineRepository timelineRepository,
                              MeetingConfigRepository meetingConfigRepository,
                              ProjectRepository projectRepository,
                              TeamRepository teamRepository,
                              NotificationService notificationService,
                              ObjectMapper objectMapper) {
        this.timelineRepository = timelineRepository;
        this.meetingConfigRepository = meetingConfigRepository;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Timeline saveTimeline(TimelineCreateRequest request) {
        Optional<Timeline> existingOpt = timelineRepository.findByFormId(request.formId());
        Timeline timeline;
        
        if (existingOpt.isPresent()) {
            timeline = existingOpt.get();
        } else {
            timeline = new Timeline();
            timeline.setTimelineId(IdGenerator.generate("tm_"));
            timeline.setFormId(request.formId());
        }

        timeline.setSynopsisDate(request.synopsisDate());
        timeline.setProgress1Date(request.progress1Date());
        timeline.setProgress2Date(request.progress2Date());
        timeline.setFinalSubmissionDate(request.finalSubmissionDate());

        timeline = timelineRepository.save(timeline);

        notifyFormUsers(request.formId(), "Admin has updated the project timeline for your batch.");
        return timeline;
    }

    @Transactional
    public MeetingConfig saveMeetingConfig(MeetingConfigCreateRequest request) {
        Optional<MeetingConfig> existingOpt = meetingConfigRepository.findByFormId(request.formId());
        MeetingConfig config;

        if (existingOpt.isPresent()) {
            config = existingOpt.get();
        } else {
            config = new MeetingConfig();
            config.setConfigId(IdGenerator.generate("mc_"));
            config.setFormId(request.formId());
        }

        config.setSynopsisRequired(request.synopsisRequired());
        config.setProgress1Required(request.progress1Required());
        config.setProgress2Required(request.progress2Required());
        config.setFinalRequired(request.finalRequired());

        return meetingConfigRepository.save(config);
    }

    public Timeline getTimelineByFormId(String formId) {
        return timelineRepository.findByFormId(formId).orElse(null);
    }

    public MeetingConfig getMeetingConfigByFormId(String formId) {
        return meetingConfigRepository.findByFormId(formId).orElse(null);
    }

    private void notifyFormUsers(String formId, String message) {
        List<Project> projects = projectRepository.findByFormId(formId);
        
        for (Project project : projects) {
            if (project.getSupervisorId() != null && !project.getSupervisorId().isEmpty()) {
                notificationService.createNotification(project.getSupervisorId(), "supervisor", message);
            }
            
            teamRepository.findById(project.getTeamId()).ifPresent(team -> {
                notificationService.createNotification(team.getLeaderId(), "student", message);
                try {
                    List<String> memberIds = objectMapper.readValue(team.getTeamMemberArray(), new TypeReference<List<String>>() {});
                    for (String memberId : memberIds) {
                         notificationService.createNotification(memberId, "student", message);
                    }
                } catch (Exception ignored) {}
            });
        }
    }
}

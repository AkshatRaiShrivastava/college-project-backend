package com.akshat.college_project.service;

import com.akshat.college_project.entity.Meeting;
import com.akshat.college_project.entity.MeetingConfig;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.Supervisor;
import com.akshat.college_project.entity.Timeline;
import com.akshat.college_project.entity.enums.MeetingStatus;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.MeetingConfigRepository;
import com.akshat.college_project.repository.MeetingRepository;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.repository.SupervisorRepository;
import com.akshat.college_project.repository.TimelineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MeetingValidationScheduler {

    private static final Logger log = LoggerFactory.getLogger(MeetingValidationScheduler.class);
    private final TimelineRepository timelineRepository;
    private final ProjectRepository projectRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingConfigRepository meetingConfigRepository;
    private final SupervisorRepository supervisorRepository;
    private final NotificationService notificationService;

    public MeetingValidationScheduler(TimelineRepository timelineRepository,
                                      ProjectRepository projectRepository,
                                      MeetingRepository meetingRepository,
                                      MeetingConfigRepository meetingConfigRepository,
                                      SupervisorRepository supervisorRepository,
                                      NotificationService notificationService) {
        this.timelineRepository = timelineRepository;
        this.projectRepository = projectRepository;
        this.meetingRepository = meetingRepository;
        this.meetingConfigRepository = meetingConfigRepository;
        this.supervisorRepository = supervisorRepository;
        this.notificationService = notificationService;
    }

    // Runs every day at midnight to process and deduct points for missed meetings
    @Scheduled(cron = "0 0 0 * * ?")
    public void validateMeetingsAndApplyPenalties() {
        log.info("Starting Daily Meeting Validation Scheduler...");
        List<Timeline> timelines = timelineRepository.findAll();
        Instant now = Instant.now();

        for (Timeline timeline : timelines) {
            MeetingConfig config = meetingConfigRepository.findByFormId(timeline.getFormId()).orElse(null);
            if (config == null) continue;

            List<Project> projects = projectRepository.findByFormId(timeline.getFormId());

            for (Project project : projects) {
                if (project.getSupervisorId() == null || project.getSupervisorId().isEmpty()) continue;

                checkAndPenalize(project, StageStatus.SYNOPSIS, timeline.getSynopsisDate(), config.getSynopsisRequired(), now);
                checkAndPenalize(project, StageStatus.PROGRESS1, timeline.getProgress1Date(), config.getProgress1Required(), now);
                checkAndPenalize(project, StageStatus.PROGRESS2, timeline.getProgress2Date(), config.getProgress2Required(), now);
                checkAndPenalize(project, StageStatus.FINAL, timeline.getFinalSubmissionDate(), config.getFinalRequired(), now);
            }
        }
    }

    private void checkAndPenalize(Project project, StageStatus stage, Instant deadline, Integer requiredMeetings, Instant now) {
        if (deadline == null || requiredMeetings == null || requiredMeetings <= 0) return;

        // If the deadline just passed (within the last 24 hours)
        if (now.isAfter(deadline) && now.minus(24, ChronoUnit.HOURS).isBefore(deadline)) {
            List<Meeting> meetings = meetingRepository.findByProjectIdAndStage(project.getProjectId(), stage);
            long completedMeetings = meetings.stream()
                    .filter(m -> m.getStatus() == MeetingStatus.COMPLETED)
                    .count();

            if (completedMeetings < requiredMeetings) {
                long shortfall = requiredMeetings - completedMeetings;
                double penalty = shortfall * 5.0; // 5 points per missed meeting (requirement specified by user)
                
                supervisorRepository.findById(project.getSupervisorId()).ifPresent(supervisor -> {
                    double currentScore = supervisor.getPerformanceScore() != null ? supervisor.getPerformanceScore() : 100.0;
                    supervisor.setPerformanceScore(Math.max(0, currentScore - penalty));
                    supervisorRepository.save(supervisor);

                    notificationService.createNotification(supervisor.getSupervisorId(), "supervisor", 
                        "Performance Penalty: You lost " + penalty + " points because you missed " + shortfall + " required meetings for Stage " + stage);
                });
            }
        }
    }
}

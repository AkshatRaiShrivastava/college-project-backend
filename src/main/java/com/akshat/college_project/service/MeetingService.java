package com.akshat.college_project.service;

import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.MeetingCreateRequest;
import com.akshat.college_project.dto.MeetingExecuteRequest;
import com.akshat.college_project.entity.Meeting;
import com.akshat.college_project.entity.MeetingAttendance;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.Student;
import com.akshat.college_project.entity.Team;
import com.akshat.college_project.entity.enums.MeetingStatus;
import com.akshat.college_project.repository.MeetingAttendanceRepository;
import com.akshat.college_project.repository.MeetingRepository;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.repository.StudentRepository;
import com.akshat.college_project.repository.TeamRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingAttendanceRepository meetingAttendanceRepository;
    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;
    private final TeamRepository teamRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public MeetingService(MeetingRepository meetingRepository,
                          MeetingAttendanceRepository meetingAttendanceRepository,
                          ProjectRepository projectRepository,
                          StudentRepository studentRepository,
                          TeamRepository teamRepository,
                          NotificationService notificationService,
                          ObjectMapper objectMapper) {
        this.meetingRepository = meetingRepository;
        this.meetingAttendanceRepository = meetingAttendanceRepository;
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.teamRepository = teamRepository;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Meeting scheduleMeeting(MeetingCreateRequest request) {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(IdGenerator.generate("mtg_"));
        meeting.setProjectId(request.projectId());
        meeting.setSupervisorId(request.supervisorId());
        meeting.setStage(request.stage());
        meeting.setMeetingDate(request.meetingDate());
        meeting.setMeetingTime(request.meetingTime());
        meeting.setMode(request.mode());
        meeting.setLocationOrLink(request.locationOrLink());

        meeting = meetingRepository.save(meeting);

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));

        notifyTeamMembers(project.getTeamId(), "New meeting scheduled by your supervisor on " + request.meetingDate());

        return meeting;
    }

    @Transactional
    public Meeting executeMeeting(String meetingId, MeetingExecuteRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found: " + meetingId));

        Project project = projectRepository.findById(meeting.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + meeting.getProjectId()));

        List<String> allStudents = getTeamMemberIds(project.getTeamId());

        for (String studentId : allStudents) {
            boolean isPresent = request.presentStudentIds().contains(studentId);

            MeetingAttendance attendance = new MeetingAttendance();
            attendance.setAttendanceId(IdGenerator.generate("mat_"));
            attendance.setMeetingId(meetingId);
            attendance.setStudentId(studentId);
            attendance.setIsPresent(isPresent);
            meetingAttendanceRepository.save(attendance);

            // Deduct 10 points for absent students
            if (!isPresent) {
                studentRepository.findById(studentId).ifPresent(student -> {
                    double currentScore = student.getPerformanceScore() != null ? student.getPerformanceScore() : 100.0;
                    student.setPerformanceScore(Math.max(0, currentScore - 10.0));
                    studentRepository.save(student);
                    
                    notificationService.createNotification(studentId, "student", 
                        "You lost 10 performance points for missing the meeting on " + meeting.getMeetingDate());
                });
            }
        }

        meeting.setConclusionNotes(request.conclusionNotes());
        meeting.setStatus(MeetingStatus.COMPLETED);
        
        notifyTeamMembers(project.getTeamId(), "Meeting notes and attendance have been uploaded by your supervisor.");
        
        return meetingRepository.save(meeting);
    }

    public List<Meeting> getMeetingsByProject(String projectId) {
        return meetingRepository.findByProjectId(projectId);
    }

    public List<Meeting> getMeetingsBySupervisor(String supervisorId) {
        return meetingRepository.findBySupervisorId(supervisorId);
    }

    private List<String> getTeamMemberIds(String teamId) {
        List<String> userIds = new ArrayList<>();
        teamRepository.findById(teamId).ifPresent(team -> {
            userIds.add(team.getLeaderId());
            try {
                List<String> members = objectMapper.readValue(team.getTeamMemberArray(), new TypeReference<List<String>>() {});
                userIds.addAll(members);
            } catch (Exception ignored) {}
        });
        return userIds;
    }

    private void notifyTeamMembers(String teamId, String message) {
        List<String> memberIds = getTeamMemberIds(teamId);
        for (String memberId : memberIds) {
             notificationService.createNotification(memberId, "student", message);
        }
    }
}

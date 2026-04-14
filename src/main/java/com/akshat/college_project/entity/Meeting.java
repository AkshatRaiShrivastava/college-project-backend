package com.akshat.college_project.entity;

import com.akshat.college_project.entity.enums.MeetingMode;
import com.akshat.college_project.entity.enums.MeetingStatus;
import com.akshat.college_project.entity.enums.StageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meeting")
public class Meeting {

    @Id
    @Column(name = "meeting_id", length = 30)
    private String meetingId;

    @Column(name = "project_id", nullable = false, length = 30)
    private String projectId;

    @Column(name = "supervisor_id", nullable = false, length = 30)
    private String supervisorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false, length = 50)
    private StageStatus stage;

    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;

    @Column(name = "meeting_time", nullable = false)
    private LocalTime meetingTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 20)
    private MeetingMode mode;

    @Column(name = "location_or_link", length = 255)
    private String locationOrLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MeetingStatus status;

    @Column(name = "conclusion_notes", columnDefinition = "text")
    private String conclusionNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = MeetingStatus.SCHEDULED;
        }
    }
}

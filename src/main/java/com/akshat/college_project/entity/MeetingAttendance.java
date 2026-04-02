package com.akshat.college_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meeting_attendance")
public class MeetingAttendance {

    @Id
    @Column(name = "attendance_id", length = 30)
    private String attendanceId;

    @Column(name = "meeting_id", nullable = false, length = 30)
    private String meetingId;

    @Column(name = "student_id", nullable = false, length = 30)
    private String studentId;

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent;
}

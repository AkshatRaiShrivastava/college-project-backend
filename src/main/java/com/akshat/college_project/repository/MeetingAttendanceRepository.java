package com.akshat.college_project.repository;

import com.akshat.college_project.entity.MeetingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingAttendanceRepository extends JpaRepository<MeetingAttendance, String> {
    List<MeetingAttendance> findByMeetingId(String meetingId);
    List<MeetingAttendance> findByStudentId(String studentId);
}

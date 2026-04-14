package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Meeting;
import com.akshat.college_project.entity.enums.StageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, String> {
    List<Meeting> findByProjectId(String projectId);
    List<Meeting> findByProjectIdAndStage(String projectId, StageStatus stage);
    List<Meeting> findBySupervisorId(String supervisorId);
}

package com.akshat.college_project.repository;

import com.akshat.college_project.entity.SupervisorHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupervisorHistoryRepository extends JpaRepository<SupervisorHistory, String> {
    List<SupervisorHistory> findByProjectIdOrderByCreatedAtDesc(String projectId);
}

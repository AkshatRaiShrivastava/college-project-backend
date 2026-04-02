package com.akshat.college_project.repository;

import com.akshat.college_project.entity.MeetingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingConfigRepository extends JpaRepository<MeetingConfig, String> {
    Optional<MeetingConfig> findByFormId(String formId);
}

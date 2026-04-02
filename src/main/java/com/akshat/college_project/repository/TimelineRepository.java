package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, String> {
    Optional<Timeline> findByFormId(String formId);
}

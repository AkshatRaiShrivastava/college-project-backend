package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Progress1Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Progress1SubmissionRepository extends JpaRepository<Progress1Submission, String> {

    List<Progress1Submission> findByDocumentId(String documentId);
}

package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Progress2Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Progress2SubmissionRepository extends JpaRepository<Progress2Submission, String> {

    List<Progress2Submission> findByDocumentId(String documentId);
}

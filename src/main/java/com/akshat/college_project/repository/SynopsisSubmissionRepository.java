package com.akshat.college_project.repository;

import com.akshat.college_project.entity.SynopsisSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SynopsisSubmissionRepository extends JpaRepository<SynopsisSubmission, String> {

    List<SynopsisSubmission> findByDocumentId(String documentId);
}

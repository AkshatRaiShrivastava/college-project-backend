package com.akshat.college_project.repository;

import com.akshat.college_project.entity.FinalSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinalSubmissionRepository extends JpaRepository<FinalSubmission, String> {

    List<FinalSubmission> findByDocumentId(String documentId);
}

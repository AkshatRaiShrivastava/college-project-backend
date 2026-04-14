package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, String> {

    Optional<Document> findByProjectId(String projectId);
}

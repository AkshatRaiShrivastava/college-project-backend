package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {

    boolean existsByTeamId(String teamId);

    Optional<Project> findByTeamId(String teamId);

    Optional<Project> findByDocumentId(String documentId);
}

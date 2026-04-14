package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, String> {

    boolean existsByLeaderId(String leaderId);
}

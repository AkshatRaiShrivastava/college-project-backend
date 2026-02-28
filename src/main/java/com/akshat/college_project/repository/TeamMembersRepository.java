package com.akshat.college_project.repository;

import com.akshat.college_project.entity.TeamMembers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMembersRepository extends JpaRepository<TeamMembers, String> {
}

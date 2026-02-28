package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupervisorRepository extends JpaRepository<Supervisor, String> {

    boolean existsByMail(String mail);
}

package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupervisorRepository extends JpaRepository<Supervisor, String> {

    boolean existsByMail(String mail);
    boolean existsByMailIgnoreCase(String mail);
    Optional<Supervisor> findByMail(String mail);
}

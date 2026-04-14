package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {

    boolean existsByMail(String mail);
    boolean existsByMailIgnoreCase(String mail);
}

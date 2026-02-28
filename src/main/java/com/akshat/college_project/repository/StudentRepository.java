package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {

    boolean existsByMail(String mail);

    boolean existsByRollNo(String rollNo);
}

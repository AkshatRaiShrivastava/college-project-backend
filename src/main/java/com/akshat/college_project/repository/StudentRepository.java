package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String>, JpaSpecificationExecutor<Student> {

    boolean existsByMail(String mail);
    boolean existsByMailIgnoreCase(String mail);
    java.util.Optional<Student> findByMailIgnoreCase(String mail);

    boolean existsByRollNo(String rollNo);

    @Query("SELECT DISTINCT s.branch FROM Student s WHERE s.branch IS NOT NULL AND s.branch != '' ORDER BY s.branch")
    List<String> findDistinctBranches();

    @Query("SELECT DISTINCT s.batch FROM Student s WHERE s.batch IS NOT NULL AND s.batch != '' ORDER BY s.batch")
    List<String> findDistinctBatches();
}

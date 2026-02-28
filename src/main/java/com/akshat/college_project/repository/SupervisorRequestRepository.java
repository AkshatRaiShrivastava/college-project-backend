package com.akshat.college_project.repository;

import com.akshat.college_project.entity.SupervisorRequest;
import com.akshat.college_project.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupervisorRequestRepository extends JpaRepository<SupervisorRequest, Long> {

    List<SupervisorRequest> findByStatus(RequestStatus status);
}

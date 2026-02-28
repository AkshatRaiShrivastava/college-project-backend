package com.akshat.college_project.service;

import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SupervisorRequestCreateRequest;
import com.akshat.college_project.dto.SupervisorRequestUpdateRequest;
import com.akshat.college_project.entity.SupervisorRequest;
import com.akshat.college_project.entity.enums.RequestStatus;
import com.akshat.college_project.repository.SupervisorRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupervisorRequestService {

    private final SupervisorRequestRepository supervisorRequestRepository;

    public SupervisorRequestService(SupervisorRequestRepository supervisorRequestRepository) {
        this.supervisorRequestRepository = supervisorRequestRepository;
    }

    public SupervisorRequest create(SupervisorRequestCreateRequest request) {
        SupervisorRequest supervisorRequest = new SupervisorRequest();
        supervisorRequest.setSupervisorName(request.supervisorName());
        supervisorRequest.setDepartment(request.department());
        supervisorRequest.setMail(request.mail());
        supervisorRequest.setPhoneNumber(request.phoneNumber());
        return supervisorRequestRepository.save(supervisorRequest);
    }

    public SupervisorRequest get(Long requestId) {
        return supervisorRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));
    }

    public List<SupervisorRequest> getAll(RequestStatus status) {
        if (status != null) {
            return supervisorRequestRepository.findByStatus(status);
        }
        return supervisorRequestRepository.findAll();
    }

    public SupervisorRequest update(Long requestId, SupervisorRequestUpdateRequest request) {
        SupervisorRequest supervisorRequest = get(requestId);

        if (request.supervisorName() != null) {
            supervisorRequest.setSupervisorName(request.supervisorName());
        }
        if (request.department() != null) {
            supervisorRequest.setDepartment(request.department());
        }
        if (request.mail() != null) {
            supervisorRequest.setMail(request.mail());
        }
        if (request.phoneNumber() != null) {
            supervisorRequest.setPhoneNumber(request.phoneNumber());
        }
        if (request.status() != null) {
            supervisorRequest.setStatus(request.status());
        }

        return supervisorRequestRepository.save(supervisorRequest);
    }

    public void delete(Long requestId) {
        SupervisorRequest supervisorRequest = get(requestId);
        supervisorRequestRepository.delete(supervisorRequest);
    }
}

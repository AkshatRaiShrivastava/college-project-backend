package com.akshat.college_project.service;

import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SupervisorRequestCreateRequest;
import com.akshat.college_project.dto.SupervisorRequestUpdateRequest;
import com.akshat.college_project.entity.Supervisor;
import com.akshat.college_project.entity.SupervisorRequest;
import com.akshat.college_project.entity.enums.RequestStatus;
import com.akshat.college_project.repository.SupervisorRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupervisorRequestService {

    private final SupervisorRequestRepository supervisorRequestRepository;
    private final com.akshat.college_project.repository.SupervisorRepository supervisorRepository;

    public SupervisorRequestService(SupervisorRequestRepository supervisorRequestRepository, com.akshat.college_project.repository.SupervisorRepository supervisorRepository) {
        this.supervisorRequestRepository = supervisorRequestRepository;
        this.supervisorRepository = supervisorRepository;
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

    public SupervisorRequest approveRequest(Long requestId, String adminId) {
        SupervisorRequest request = get(requestId);
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Request is not in PENDING state");
        }

        // Check for duplicate supervisor email
        if (supervisorRepository.findByMail(request.getMail()).isPresent()) {
            throw new IllegalArgumentException("A supervisor with this email already exists");
        }

        // Generate temporary supervisor credential
        Supervisor supervisor = new Supervisor();
        supervisor.setSupervisorId(com.akshat.college_project.common.IdGenerator.generate("sup_"));
        supervisor.setName(request.getSupervisorName());
        supervisor.setMail(request.getMail());
        supervisor.setBranch(request.getDepartment());
        
        // Random 8 character password for temp login
        String tempPassword = "Temp@" + java.util.UUID.randomUUID().toString().substring(0, 6);
        supervisor.setPassword(tempPassword); 
        supervisor.setOtpVerified(false); // Admin requires them to login and verify
        
        supervisorRepository.save(supervisor);

        // Update Request Audit Log
        request.setStatus(RequestStatus.APPROVED);
        request.setApprovedBy(adminId);
        request.setApprovedAt(java.time.Instant.now());
        
        return supervisorRequestRepository.save(request);
    }

    public SupervisorRequest rejectRequest(Long requestId, String reason) {
        SupervisorRequest request = get(requestId);
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Request is not in PENDING state");
        }
        
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        return supervisorRequestRepository.save(request);
    }
}

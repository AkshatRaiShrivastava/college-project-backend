package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SupervisorCreateRequest;
import com.akshat.college_project.dto.SupervisorUpdateRequest;
import com.akshat.college_project.entity.Supervisor;
import com.akshat.college_project.entity.enums.AccountType;
import com.akshat.college_project.repository.SupervisorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupervisorService {

    private final SupervisorRepository supervisorRepository;
    private final OtpService otpService;

    public SupervisorService(SupervisorRepository supervisorRepository, OtpService otpService) {
        this.supervisorRepository = supervisorRepository;
        this.otpService = otpService;
    }

    @Transactional
    public Supervisor create(SupervisorCreateRequest request) {
        String normalizedMail = normalizeEmail(request.mail());

        if (supervisorRepository.existsByMailIgnoreCase(normalizedMail)) {
            throw new BadRequestException("Supervisor mail already exists");
        }

        otpService.consumeOtpForAccountCreation(normalizedMail, request.otpCode(), AccountType.SUPERVISOR);

        Supervisor supervisor = new Supervisor();
        supervisor.setSupervisorId(IdGenerator.generate("sup_"));
        supervisor.setName(request.name());
        supervisor.setMail(normalizedMail);
        supervisor.setPassword(request.password());
        supervisor.setBranch(request.branch());
        supervisor.setEnrollStatus(request.enrollStatus());
        supervisor.setOtpVerified(Boolean.TRUE);
        return supervisorRepository.save(supervisor);
    }

    public Supervisor get(String supervisorId) {
        return supervisorRepository.findById(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found: " + supervisorId));
    }

    public List<Supervisor> getAll() {
        return supervisorRepository.findAll();
    }

    public Supervisor update(String supervisorId, SupervisorUpdateRequest request) {
        Supervisor supervisor = get(supervisorId);
        String updatedMail = request.mail() == null ? null : normalizeEmail(request.mail());

        if (updatedMail != null && !updatedMail.equalsIgnoreCase(supervisor.getMail())
                && supervisorRepository.existsByMailIgnoreCase(updatedMail)) {
            throw new BadRequestException("Supervisor mail already exists");
        }

        if (request.name() != null) {
            supervisor.setName(request.name());
        }
        if (updatedMail != null) {
            supervisor.setMail(updatedMail);
        }
        if (request.password() != null) {
            supervisor.setPassword(request.password());
        }
        if (request.branch() != null) {
            supervisor.setBranch(request.branch());
        }
        if (request.enrollStatus() != null) {
            supervisor.setEnrollStatus(request.enrollStatus());
        }

        return supervisorRepository.save(supervisor);
    }

    public void delete(String supervisorId) {
        Supervisor supervisor = get(supervisorId);
        supervisorRepository.delete(supervisor);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isBlank()) {
            throw new BadRequestException("Supervisor mail is required");
        }
        return email.trim().toLowerCase();
    }
}

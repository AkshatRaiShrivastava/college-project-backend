package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.SupervisorCreateRequest;
import com.akshat.college_project.dto.SupervisorUpdateRequest;
import com.akshat.college_project.entity.Supervisor;
import com.akshat.college_project.repository.SupervisorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupervisorService {

    private final SupervisorRepository supervisorRepository;

    public SupervisorService(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    public Supervisor create(SupervisorCreateRequest request) {
        if (supervisorRepository.existsByMail(request.mail())) {
            throw new BadRequestException("Supervisor mail already exists");
        }

        Supervisor supervisor = new Supervisor();
        supervisor.setSupervisorId(IdGenerator.generate("sup_"));
        supervisor.setName(request.name());
        supervisor.setMail(request.mail());
        supervisor.setPassword(request.password());
        supervisor.setBranch(request.branch());
        supervisor.setEnrollStatus(request.enrollStatus());
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

        if (request.mail() != null && !request.mail().equalsIgnoreCase(supervisor.getMail())
                && supervisorRepository.existsByMail(request.mail())) {
            throw new BadRequestException("Supervisor mail already exists");
        }

        if (request.name() != null) {
            supervisor.setName(request.name());
        }
        if (request.mail() != null) {
            supervisor.setMail(request.mail());
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
}

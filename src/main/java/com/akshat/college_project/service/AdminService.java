package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.AdminCreateRequest;
import com.akshat.college_project.dto.AdminUpdateRequest;
import com.akshat.college_project.entity.Admin;
import com.akshat.college_project.entity.enums.AccountType;
import com.akshat.college_project.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final OtpService otpService;

    public AdminService(AdminRepository adminRepository, OtpService otpService) {
        this.adminRepository = adminRepository;
        this.otpService = otpService;
    }

    @Transactional
    public Admin create(AdminCreateRequest request) {
        String normalizedMail = normalizeEmail(request.mail());

        if (adminRepository.existsByMailIgnoreCase(normalizedMail)) {
            throw new BadRequestException("Admin mail already exists");
        }

        otpService.consumeOtpForAccountCreation(normalizedMail, request.otpCode(), AccountType.ADMIN);

        Admin admin = new Admin();
        admin.setAdminId(IdGenerator.generate("adm_"));
        admin.setName(request.name());
        admin.setMail(normalizedMail);
        admin.setPassword(request.password());
        admin.setDepartment(request.department());
        admin.setOtpVerified(Boolean.TRUE);
        return adminRepository.save(admin);
    }

    public Admin get(String adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + adminId));
    }

    public List<Admin> getAll() {
        return adminRepository.findAll();
    }

    public Admin update(String adminId, AdminUpdateRequest request) {
        Admin admin = get(adminId);
        String updatedMail = request.mail() == null ? null : normalizeEmail(request.mail());

        if (updatedMail != null && !updatedMail.equalsIgnoreCase(admin.getMail())
                && adminRepository.existsByMailIgnoreCase(updatedMail)) {
            throw new BadRequestException("Admin mail already exists");
        }

        if (request.name() != null) {
            admin.setName(request.name());
        }
        if (updatedMail != null) {
            admin.setMail(updatedMail);
        }
        if (request.password() != null) {
            admin.setPassword(request.password());
        }
        if (request.department() != null) {
            admin.setDepartment(request.department());
        }

        return adminRepository.save(admin);
    }

    public void delete(String adminId) {
        Admin admin = get(adminId);
        adminRepository.delete(admin);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isBlank()) {
            throw new BadRequestException("Admin mail is required");
        }
        return email.trim().toLowerCase();
    }
}

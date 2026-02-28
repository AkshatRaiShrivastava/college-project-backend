package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.AdminCreateRequest;
import com.akshat.college_project.dto.AdminUpdateRequest;
import com.akshat.college_project.entity.Admin;
import com.akshat.college_project.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin create(AdminCreateRequest request) {
        if (adminRepository.existsByMail(request.mail())) {
            throw new BadRequestException("Admin mail already exists");
        }

        Admin admin = new Admin();
        admin.setAdminId(IdGenerator.generate("adm_"));
        admin.setName(request.name());
        admin.setMail(request.mail());
        admin.setPassword(request.password());
        admin.setDepartment(request.department());
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

        if (request.mail() != null && !request.mail().equalsIgnoreCase(admin.getMail())
                && adminRepository.existsByMail(request.mail())) {
            throw new BadRequestException("Admin mail already exists");
        }

        if (request.name() != null) {
            admin.setName(request.name());
        }
        if (request.mail() != null) {
            admin.setMail(request.mail());
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
}

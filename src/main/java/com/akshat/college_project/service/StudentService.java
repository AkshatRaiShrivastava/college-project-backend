package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.StudentCreateRequest;
import com.akshat.college_project.dto.StudentUpdateRequest;
import com.akshat.college_project.entity.Student;
import com.akshat.college_project.entity.enums.AccountType;
import com.akshat.college_project.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final OtpService otpService;

    public StudentService(StudentRepository studentRepository, OtpService otpService) {
        this.studentRepository = studentRepository;
        this.otpService = otpService;
    }

    @Transactional
    public Student create(StudentCreateRequest request) {
        String normalizedMail = normalizeEmail(request.mail());

        if (studentRepository.existsByMailIgnoreCase(normalizedMail)) {
            throw new BadRequestException("Student mail already exists");
        }
        if (studentRepository.existsByRollNo(request.rollNo())) {
            throw new BadRequestException("Student roll number already exists");
        }

        otpService.consumeOtpForAccountCreation(normalizedMail, request.otpCode(), AccountType.STUDENT);

        Student student = new Student();
        student.setStudentId(IdGenerator.generate("stu_"));
        student.setName(request.name());
        student.setMail(normalizedMail);
        student.setPassword(request.password());
        student.setRollNo(request.rollNo());
        student.setBranch(request.branch());
        student.setBatch(request.batch());
        student.setEnrollStatus(request.enrollStatus());
        student.setOtpVerified(Boolean.TRUE);
        return studentRepository.save(student);
    }

    public Student get(String studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public Student update(String studentId, StudentUpdateRequest request) {
        Student student = get(studentId);

        String updatedMail = request.mail() == null ? null : normalizeEmail(request.mail());

        if (updatedMail != null && !updatedMail.equalsIgnoreCase(student.getMail())
                && studentRepository.existsByMailIgnoreCase(updatedMail)) {
            throw new BadRequestException("Student mail already exists");
        }

        if (request.rollNo() != null && !request.rollNo().equalsIgnoreCase(student.getRollNo())
                && studentRepository.existsByRollNo(request.rollNo())) {
            throw new BadRequestException("Student roll number already exists");
        }

        if (request.name() != null) {
            student.setName(request.name());
        }
        if (updatedMail != null) {
            student.setMail(updatedMail);
        }
        if (request.password() != null) {
            student.setPassword(request.password());
        }
        if (request.rollNo() != null) {
            student.setRollNo(request.rollNo());
        }
        if (request.branch() != null) {
            student.setBranch(request.branch());
        }
        if (request.batch() != null) {
            student.setBatch(request.batch());
        }
        if (request.enrollStatus() != null) {
            student.setEnrollStatus(request.enrollStatus());
        }

        return studentRepository.save(student);
    }

    public void delete(String studentId) {
        Student student = get(studentId);
        studentRepository.delete(student);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.trim().isBlank()) {
            throw new BadRequestException("Student mail is required");
        }
        return email.trim().toLowerCase();
    }
}

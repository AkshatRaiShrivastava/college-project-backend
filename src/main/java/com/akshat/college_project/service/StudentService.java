package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.StudentCreateRequest;
import com.akshat.college_project.dto.StudentUpdateRequest;
import com.akshat.college_project.entity.Student;
import com.akshat.college_project.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student create(StudentCreateRequest request) {
        if (studentRepository.existsByMail(request.mail())) {
            throw new BadRequestException("Student mail already exists");
        }
        if (studentRepository.existsByRollNo(request.rollNo())) {
            throw new BadRequestException("Student roll number already exists");
        }

        Student student = new Student();
        student.setStudentId(IdGenerator.generate("stu_"));
        student.setName(request.name());
        student.setMail(request.mail());
        student.setPassword(request.password());
        student.setRollNo(request.rollNo());
        student.setBranch(request.branch());
        student.setBatch(request.batch());
        student.setEnrollStatus(request.enrollStatus());
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

        if (request.mail() != null && !request.mail().equalsIgnoreCase(student.getMail())
                && studentRepository.existsByMail(request.mail())) {
            throw new BadRequestException("Student mail already exists");
        }

        if (request.rollNo() != null && !request.rollNo().equalsIgnoreCase(student.getRollNo())
                && studentRepository.existsByRollNo(request.rollNo())) {
            throw new BadRequestException("Student roll number already exists");
        }

        if (request.name() != null) {
            student.setName(request.name());
        }
        if (request.mail() != null) {
            student.setMail(request.mail());
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
}

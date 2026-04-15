package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.controller.StudentInviteController.StudentInviteRequest;
import com.akshat.college_project.entity.Student;
import com.akshat.college_project.entity.enums.StudentEnrollStatus;
import com.akshat.college_project.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StudentInviteService {

    private final StudentRepository studentRepository;
    private final MailService mailService;
    private static final Pattern ROLL_NO_PATTERN = Pattern.compile("^(\\d{2})([A-Za-z]+)(\\d+)$");

    public StudentInviteService(StudentRepository studentRepository, MailService mailService) {
        this.studentRepository = studentRepository;
        this.mailService = mailService;
    }

    @Transactional
    public void inviteStudent(StudentInviteRequest request) {
        String email = request.email() != null ? request.email().trim().toLowerCase() : null;
        String rollNo = request.roll_number() != null ? request.roll_number().trim().toUpperCase() : null;
        String name = request.name() != null ? request.name().trim() : null;

        if (email == null || email.isBlank() || rollNo == null || rollNo.isBlank() || name == null || name.isBlank()) {
            throw new BadRequestException("Required fields missing");
        }

        if (studentRepository.existsByMailIgnoreCase(email) || studentRepository.existsByRollNo(rollNo)) {
            throw new BadRequestException("Student already exists");
        }

        String batch = request.batch();
        String branch = request.branch();

        if ((batch == null || batch.isBlank()) || (branch == null || branch.isBlank())) {
            Matcher matcher = ROLL_NO_PATTERN.matcher(rollNo);
            if (matcher.matches()) {
                if (batch == null || batch.isBlank()) batch = "20" + matcher.group(1);
                if (branch == null || branch.isBlank()) branch = matcher.group(2).toUpperCase();
            } else {
                if (batch == null || batch.isBlank()) batch = "UNKNOWN";
                if (branch == null || branch.isBlank()) branch = "UNKNOWN";
            }
        }

        Student student = new Student();
        student.setStudentId(IdGenerator.generate("stu_"));
        student.setName(name);
        student.setMail(email);
        student.setRollNo(rollNo);
        student.setBatch(batch);
        student.setBranch(branch);
        student.setPassword("DEFAULT_PASSWORD_123");
        student.setEnrollStatus(StudentEnrollStatus.PENDING);
        student.setPerformanceScore(50.0);
        student.setOtpVerified(false);

        studentRepository.save(student);

        try {
            mailService.sendStudentInviteMail(email, name, rollNo, branch, batch);
        } catch (Exception e) {
            // System implicitly catches and logs but proceeds since Student is saved
            System.err.println("SMTP Mail delivery failed for invited student: " + email);
        }
    }
}

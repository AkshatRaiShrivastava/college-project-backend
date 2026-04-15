package com.akshat.college_project.controller;

import com.akshat.college_project.dto.OtpActionResponse;
import com.akshat.college_project.service.StudentInviteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class StudentInviteController {

    private final StudentInviteService studentInviteService;

    public StudentInviteController(StudentInviteService studentInviteService) {
        this.studentInviteService = studentInviteService;
    }

    public record StudentInviteRequest(
            @NotBlank String name,
            @NotBlank String roll_number,
            @NotBlank @Email String email,
            String batch,
            String branch
    ) {}

    @PostMapping("/invite-student")
    public OtpActionResponse inviteStudent(@Valid @RequestBody StudentInviteRequest request) {
        studentInviteService.inviteStudent(request);
        return new OtpActionResponse("Student invited successfully");
    }
}

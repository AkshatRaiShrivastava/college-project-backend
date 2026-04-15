package com.akshat.college_project.controller;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.dto.OtpActionResponse;
import com.akshat.college_project.entity.Student;
import com.akshat.college_project.entity.enums.AccountType;
import com.akshat.college_project.service.OtpService;
import com.akshat.college_project.service.StudentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentAuthController {

    private final OtpService otpService;
    private final StudentService studentService;

    public StudentAuthController(OtpService otpService, StudentService studentService) {
        this.otpService = otpService;
        this.studentService = studentService;
    }

    public record SendOtpRequest(@NotBlank @Email String email) {}
    @PostMapping("/send-otp")
    public OtpActionResponse sendOtp(@Valid @RequestBody SendOtpRequest request) {
        otpService.sendOtpForFirstTimeVerification(request.email());
        return new OtpActionResponse("OTP sent successfully");
    }

    public record VerifyOtpRequest(@NotBlank @Email String email, @NotBlank String otp) {}
    @PostMapping("/verify-otp")
    public OtpActionResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        otpService.verifyOtp(request.email(), request.otp(), AccountType.STUDENT);
        studentService.markOtpVerified(request.email());
        return new OtpActionResponse("OTP verified successfully");
    }

    public record SetPasswordRequest(@NotBlank @Email String email, @NotBlank String password, @NotBlank String confirmPassword) {}
    @PostMapping("/set-password")
    public OtpActionResponse setPassword(@Valid @RequestBody SetPasswordRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        studentService.setupFirstTimePassword(request.email(), request.password());
        return new OtpActionResponse("Account setup complete");
    }

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}
    @PostMapping("/login")
    public Student login(@Valid @RequestBody LoginRequest request) {
        return studentService.verifyLogin(request.email(), request.password());
    }
}

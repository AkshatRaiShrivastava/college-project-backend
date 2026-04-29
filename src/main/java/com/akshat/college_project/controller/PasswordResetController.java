package com.akshat.college_project.controller;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.dto.OtpActionResponse;
import com.akshat.college_project.service.OtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/forgot-password")
public class PasswordResetController {

    private final OtpService otpService;

    public PasswordResetController(OtpService otpService) {
        this.otpService = otpService;
    }

    public record SendOtpRequest(@NotBlank @Email String email) {}
    @PostMapping("/send-otp")
    public OtpActionResponse sendOtp(@Valid @RequestBody SendOtpRequest request) {
        otpService.sendOtpForPasswordReset(request.email());
        return new OtpActionResponse("OTP sent successfully to registered email");
    }

    public record VerifyOtpRequest(@NotBlank @Email String email, @NotBlank String otp) {}
    @PostMapping("/verify-otp")
    public OtpActionResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        // OtpService will check validity against the exact account type
        var accountType = otpService.determineAccountType(request.email());
        otpService.checkValidOtp(request.email(), request.otp(), accountType);
        return new OtpActionResponse("OTP verified successfully");
    }

    public record ResetPasswordRequest(@NotBlank @Email String email, @NotBlank String password, @NotBlank String confirmPassword, @NotBlank String otp) {}
    @PostMapping("/reset")
    public OtpActionResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        if (request.password().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }
        otpService.resetPassword(request.email(), request.otp(), request.password());
        return new OtpActionResponse("Password reset successfully");
    }
}

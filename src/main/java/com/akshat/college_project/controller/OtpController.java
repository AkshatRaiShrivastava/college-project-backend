package com.akshat.college_project.controller;

import com.akshat.college_project.dto.OtpActionResponse;
import com.akshat.college_project.dto.OtpResendRequest;
import com.akshat.college_project.dto.OtpVerifyRequest;
import com.akshat.college_project.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/verify")
    public OtpActionResponse verify(@Valid @RequestBody OtpVerifyRequest request) {
        otpService.verifyOtp(request.email(), request.code(), request.accountType());
        return new OtpActionResponse("OTP verified successfully");
    }

    @PostMapping("/resend")
    public OtpActionResponse resend(@Valid @RequestBody OtpResendRequest request) {
        otpService.resendOtp(request.email(), request.accountType());
        return new OtpActionResponse("OTP sent successfully");
    }
}

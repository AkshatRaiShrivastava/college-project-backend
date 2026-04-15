package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.entity.Otp;
import com.akshat.college_project.entity.enums.AccountType;
import com.akshat.college_project.repository.AdminRepository;
import com.akshat.college_project.repository.OtpRepository;
import com.akshat.college_project.repository.StudentRepository;
import com.akshat.college_project.repository.SupervisorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final MailService mailService;
    private final StudentRepository studentRepository;
    private final SupervisorRepository supervisorRepository;
    private final AdminRepository adminRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long otpTtlMinutes;

    public OtpService(
            OtpRepository otpRepository,
            MailService mailService,
            StudentRepository studentRepository,
            SupervisorRepository supervisorRepository,
            AdminRepository adminRepository,
            @Value("${app.otp.ttl-minutes:10}") long otpTtlMinutes
    ) {
        this.otpRepository = otpRepository;
        this.mailService = mailService;
        this.studentRepository = studentRepository;
        this.supervisorRepository = supervisorRepository;
        this.adminRepository = adminRepository;
        this.otpTtlMinutes = otpTtlMinutes;
    }

    @Transactional
    public void sendOtpForAccountCreation(String email, AccountType accountType) {
        String normalizedEmail = normalizeEmail(email);
        AccountType normalizedAccountType = normalizeAccountType(accountType);

        if (accountExists(normalizedEmail, normalizedAccountType)) {
            throw new BadRequestException(normalizedAccountType + " account already exists for email: " + normalizedEmail);
        }

        expireActiveOtps(normalizedEmail, normalizedAccountType);

        Otp otp = new Otp();
        otp.setEmail(normalizedEmail);
        otp.setCode(generateOtpCode());
        otp.setAccountType(normalizedAccountType);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpTtlMinutes));
        otpRepository.save(otp);

        mailService.sendOtpMail(normalizedEmail, otp.getCode(), normalizedAccountType);
    }

    @Transactional
    public void sendOtpForFirstTimeVerification(String email) {
        String normalizedEmail = normalizeEmail(email);

        com.akshat.college_project.entity.Student student = studentRepository.findByMailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Student not registered. Contact admin."));

        if (Boolean.TRUE.equals(student.getOtpVerified())) {
            throw new BadRequestException("Already verified. Please login.");
        }

        expireActiveOtps(normalizedEmail, AccountType.STUDENT);

        Otp otp = new Otp();
        otp.setEmail(normalizedEmail);
        otp.setCode(generateOtpCode());
        otp.setAccountType(AccountType.STUDENT);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpTtlMinutes));
        otpRepository.save(otp);

        mailService.sendOtpMail(normalizedEmail, otp.getCode(), AccountType.STUDENT);
    }

    @Transactional
    public void resendOtp(String email, AccountType accountType) {
        sendOtpForAccountCreation(email, accountType);
    }

    @Transactional
    public void verifyOtp(String email, String code, AccountType accountType) {
        Otp otp = findValidOtp(email, code, accountType);
        otp.setIsUsed(Boolean.TRUE);
        otpRepository.save(otp);
    }

    @Transactional
    public void consumeOtpForAccountCreation(String email, String code, AccountType accountType) {
        verifyOtp(email, code, accountType);
    }

    private Otp findValidOtp(String email, String code, AccountType accountType) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedCode = normalizeCode(code);
        AccountType normalizedAccountType = normalizeAccountType(accountType);

        Otp otp = otpRepository.findTopByEmailAndAccountTypeAndCodeAndIsUsedFalseOrderByCreatedAtDesc(
                        normalizedEmail,
                        normalizedAccountType,
                        normalizedCode
                )
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }
        return otp;
    }

    private String generateOtpCode() {
        int value = secureRandom.nextInt(900000) + 100000;
        return Integer.toString(value);
    }

    private void expireActiveOtps(String email, AccountType accountType) {
        List<Otp> activeOtps = otpRepository.findByEmailAndAccountTypeAndIsUsedFalse(email, accountType);
        for (Otp otp : activeOtps) {
            otp.setIsUsed(Boolean.TRUE);
        }
        if (!activeOtps.isEmpty()) {
            otpRepository.saveAll(activeOtps);
        }
    }

    private boolean accountExists(String email, AccountType accountType) {
        return switch (accountType) {
            case STUDENT -> studentRepository.existsByMailIgnoreCase(email);
            case SUPERVISOR -> supervisorRepository.existsByMailIgnoreCase(email);
            case ADMIN -> adminRepository.existsByMailIgnoreCase(email);
        };
    }

    private String normalizeEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        if (normalized.isBlank()) {
            throw new BadRequestException("email is required");
        }
        return normalized;
    }

    private String normalizeCode(String code) {
        String normalized = code == null ? "" : code.trim();
        if (normalized.isBlank()) {
            throw new BadRequestException("code is required");
        }
        return normalized;
    }

    private AccountType normalizeAccountType(AccountType accountType) {
        if (accountType == null) {
            throw new BadRequestException("accountType is required");
        }
        return accountType;
    }
}

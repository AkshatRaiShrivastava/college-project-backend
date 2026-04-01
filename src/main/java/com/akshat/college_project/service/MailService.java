package com.akshat.college_project.service;

import com.akshat.college_project.entity.enums.AccountType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final long otpTtlMinutes;

    public MailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from:}") String fromAddress,
            @Value("${app.otp.ttl-minutes:10}") long otpTtlMinutes
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.otpTtlMinutes = otpTtlMinutes;
    }

    public void sendOtpMail(String toEmail, String otpCode, AccountType accountType) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(toEmail);
        message.setSubject("OTP for " + accountType + " account verification");
        message.setText(buildBody(otpCode, accountType));
        mailSender.send(message);
    }

    private String buildBody(String otpCode, AccountType accountType) {
        return "Hello,\n\n"
                + "Your OTP for " + accountType + " account verification is: " + otpCode + "\n"
                + "This OTP expires in " + otpTtlMinutes + " minutes.\n\n"
                + "If this was not requested by you, ignore this email.\n\n"
                + "- College Project Backend";
    }

    @org.springframework.scheduling.annotation.Async
    public void sendAssignmentMail(String toEmail, String projectName, String adminName) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(toEmail);
        message.setSubject("Assigned as Supervisor");
        message.setText("Dear Supervisor,\n\n"
                + "You have been assigned as supervisor for Project: " + projectName + "\n"
                + "Assigned By: " + adminName + "\n\n"
                + "Thank You,\nNYT Flow Administration");
        mailSender.send(message);
    }

    @org.springframework.scheduling.annotation.Async
    public void sendUnassignmentMail(String toEmail, String projectName, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(toEmail);
        message.setSubject("Unassigned from Project: " + projectName);
        message.setText("Dear Supervisor,\n\n"
                + "You have been unassigned from Project: " + projectName + "\n"
                + "Reason: " + (reason != null ? reason : "Administrative Reassignment") + "\n\n"
                + "Thank You,\nNYT Flow Administration");
        mailSender.send(message);
    }
}

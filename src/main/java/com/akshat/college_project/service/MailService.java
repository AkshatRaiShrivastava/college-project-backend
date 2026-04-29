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
    private final String frontendUrl;

    public MailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from:}") String fromAddress,
            @Value("${app.otp.ttl-minutes:10}") long otpTtlMinutes,
            @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.otpTtlMinutes = otpTtlMinutes;
        this.frontendUrl = frontendUrl;
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

    public void sendPasswordResetMail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("Hello,\n\n"
                + "You requested to reset your password. Your verification OTP is: " + otpCode + "\n"
                + "This OTP expires in " + otpTtlMinutes + " minutes.\n\n"
                + "If you did not request this, please ignore this email and your password will remain unchanged.\n\n"
                + "- NYT Flow Administration");
        mailSender.send(message);
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

    @org.springframework.scheduling.annotation.Async
    public void sendStudentInviteMail(String toEmail, String name, String rollNumber, String branch, String batch) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(toEmail);
        message.setSubject("You're Invited to Project Management System");
        message.setText("Hello " + name + ",\n\n"
                + "You have been invited to access the Project Management System.\n\n"
                + "Your details:\n"
                + "Roll Number: " + rollNumber + "\n"
                + "Branch: " + branch + "\n"
                + "Batch: " + batch + "\n\n"
                + "Please verify your account and set your password using the link below:\n\n"
                + frontendUrl + "/login\n\n"
                + "Click on \"First Time Student? Verify Account\"\n\n"
                + "Thanks,\nAdmin Team");
        mailSender.send(message);
    }
}

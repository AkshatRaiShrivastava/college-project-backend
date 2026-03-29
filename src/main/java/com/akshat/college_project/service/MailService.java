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

    public void sendAssignmentMail(String toEmail, String projectName) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(toEmail);
        message.setSubject("Project Assignment Notification: " + projectName);
        message.setText("Dear Supervisor,\n\n"
                + "You have been officially assigned to oversee the project: " + projectName + ".\n\n"
                + "Please log into your NYT Flow dashboard to review the team members and project details.\n\n"
                + "Thank You,\nNYT Flow Administration");
        mailSender.send(message);
    }
}

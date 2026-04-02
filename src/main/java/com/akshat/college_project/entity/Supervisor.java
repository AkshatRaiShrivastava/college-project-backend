package com.akshat.college_project.entity;

import com.akshat.college_project.entity.enums.SupervisorEnrollStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "supervisor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_supervisor_mail", columnNames = "mail")
        }
)
public class Supervisor {

    @Id
    @Column(name = "supervisor_id", length = 30)
    private String supervisorId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "mail", nullable = false, length = 150)
    private String mail;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "branch", nullable = false, length = 50)
    private String branch;

    @Column(name = "otp_verified", nullable = false, columnDefinition = "boolean default false")
    private Boolean otpVerified;

    @Column(name = "performance_score", nullable = false, columnDefinition = "float8 default 100.0")
    private Double performanceScore;

    @Column(name = "create_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "enroll_status", nullable = false, length = 20)
    private SupervisorEnrollStatus enrollStatus;

    @PrePersist
    public void prePersist() {
        if (otpVerified == null) {
            otpVerified = Boolean.FALSE;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (performanceScore == null) {
            performanceScore = 100.0;
        }
        if (enrollStatus == null) {
            enrollStatus = SupervisorEnrollStatus.ACTIVE;
        }
    }
}

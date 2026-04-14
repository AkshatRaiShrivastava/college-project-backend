package com.akshat.college_project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "admin",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_admin_mail", columnNames = "mail")
        }
)
public class Admin {

    @Id
    @Column(name = "admin_id", length = 30)
    private String adminId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "mail", nullable = false, length = 150)
    private String mail;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "otp_verified", nullable = false, columnDefinition = "boolean default false")
    private Boolean otpVerified;

    @Column(name = "create_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (otpVerified == null) {
            otpVerified = Boolean.FALSE;
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

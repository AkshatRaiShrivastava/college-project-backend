package com.akshat.college_project.entity;

import com.akshat.college_project.entity.enums.StudentEnrollStatus;
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
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_students_mail", columnNames = "mail"),
                @UniqueConstraint(name = "uk_students_rollno", columnNames = "rollno")
        }
)
public class Student {

    @Id
    @Column(name = "student_id", length = 30)
    private String studentId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "mail", nullable = false, length = 150)
    private String mail;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "rollno", nullable = false, length = 20)
    private String rollNo;

    @Column(name = "branch", nullable = false, length = 50)
    private String branch;

    @Column(name = "batch", nullable = false, length = 10)
    private String batch;

    @Column(name = "create_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "enroll_status", nullable = false, length = 20)
    private StudentEnrollStatus enrollStatus;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (enrollStatus == null) {
            enrollStatus = StudentEnrollStatus.PENDING;
        }
    }
}

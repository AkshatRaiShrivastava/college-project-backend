package com.akshat.college_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "timeline")
public class Timeline {

    @Id
    @Column(name = "timeline_id", length = 30)
    private String timelineId;

    @Column(name = "form_id", nullable = false, length = 30, unique = true)
    private String formId;

    @Column(name = "synopsis_date")
    private Instant synopsisDate;

    @Column(name = "progress1_date")
    private Instant progress1Date;

    @Column(name = "progress2_date")
    private Instant progress2Date;

    @Column(name = "final_submission_date")
    private Instant finalSubmissionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

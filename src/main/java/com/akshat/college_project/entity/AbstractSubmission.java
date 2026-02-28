package com.akshat.college_project.entity;

import com.akshat.college_project.entity.enums.CommentByRole;
import com.akshat.college_project.entity.enums.SubmissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractSubmission {

    @Column(name = "document_id", nullable = false, length = 30)
    private String documentId;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SubmissionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_by_role", length = 20)
    private CommentByRole commentByRole;

    @Column(name = "comment_by_id", length = 30)
    private String commentById;

    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = Instant.now();
        }
        if (status == null) {
            status = SubmissionStatus.PENDING;
        }
    }
}

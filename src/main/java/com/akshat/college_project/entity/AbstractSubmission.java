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
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractSubmission {

    @Column(name = "project_id", nullable = false, length = 30)
    private String projectId;

    @Column(name = "document_id", nullable = false, length = 30)
    private String documentId;

    @Column(name = "leader_id", nullable = false, length = 30)
    private String leaderId;

    @Column(name = "version_no", nullable = false)
    private Integer versionNo;

    @Column(name = "revision_of", length = 30)
    private String revisionOf;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    @Column(name = "file_url", length = 1000)
    private String fileUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SubmissionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_by_role", length = 20)
    private CommentByRole commentByRole;

    @Column(name = "comment_by_id", length = 30)
    private String commentById;

    @Column(name = "team_review_json", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String teamReviewJson;

    @Column(name = "team_review_status", nullable = false, length = 20)
    private String teamReviewStatus;

    @Column(name = "visible_to_supervisor", nullable = false)
    private Boolean visibleToSupervisor;

    @Column(name = "visible_to_admin", nullable = false)
    private Boolean visibleToAdmin;

    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = Instant.now();
        }
        if (status == null) {
            status = SubmissionStatus.PENDING;
        }
        if (versionNo == null) {
            versionNo = 1;
        }
        if (teamReviewJson == null) {
            teamReviewJson = "[]";
        }
        if (teamReviewStatus == null) {
            teamReviewStatus = "PENDING";
        }
        if (visibleToSupervisor == null) {
            visibleToSupervisor = Boolean.FALSE;
        }
        if (visibleToAdmin == null) {
            visibleToAdmin = Boolean.FALSE;
        }
    }
}

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
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "form")
public class Form {

    @Id
    @Column(name = "form_id", length = 30)
    private String formId;

    @Column(name = "access_branch", nullable = false, length = 100)
    private String accessBranch;

    @Column(name = "access_batch", nullable = false, length = 50)
    private String accessBatch;

    @Column(name = "json_of_fields", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String jsonOfFields;

    @Column(name = "reference_files_json", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String referenceFilesJson;

    @Column(name = "create_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, length = 30)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (referenceFilesJson == null) {
            referenceFilesJson = "[]";
        }
    }
}

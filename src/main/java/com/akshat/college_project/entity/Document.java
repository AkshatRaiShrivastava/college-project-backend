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
@Table(name = "document")
public class Document {

    @Id
    @Column(name = "document_id", length = 30)
    private String documentId;

    @Column(name = "project_id", nullable = false, length = 30)
    private String projectId;

    @Column(name = "synopsis_id", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String synopsisId;

    @Column(name = "progress1_id", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String progress1Id;

    @Column(name = "progress2_id", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String progress2Id;

    @Column(name = "final_id", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String finalId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (synopsisId == null) {
            synopsisId = "[]";
        }
        if (progress1Id == null) {
            progress1Id = "[]";
        }
        if (progress2Id == null) {
            progress2Id = "[]";
        }
        if (finalId == null) {
            finalId = "[]";
        }
    }
}

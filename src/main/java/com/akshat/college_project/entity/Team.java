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
@Table(name = "team")
public class Team {

    @Id
    @Column(name = "team_id", length = 30)
    private String teamId;

    @Column(name = "leader_id", nullable = false, length = 30)
    private String leaderId;

    @Column(name = "project_id", length = 30)
    private String projectId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "team_length", nullable = false)
    private Integer teamLength;

    @Column(name = "team_member_array", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "CAST(? AS jsonb)")
    private String teamMemberArray;

    @Column(name = "team_complete_status", nullable = false)
    private Boolean teamCompleteStatus;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (teamLength == null) {
            teamLength = 1;
        }
        if (teamMemberArray == null) {
            teamMemberArray = "[]";
        }
        if (teamCompleteStatus == null) {
            teamCompleteStatus = Boolean.FALSE;
        }
    }
}

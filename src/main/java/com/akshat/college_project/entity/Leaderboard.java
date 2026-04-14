package com.akshat.college_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "leaderboard")
public class Leaderboard {

    @Id
    @Column(name = "project_id", length = 30)
    private String projectId;

    @Column(name = "team_id", nullable = false, length = 30)
    private String teamId;

    @Column(name = "supervisor_id", length = 30)
    private String supervisorId;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "score")
    private Integer score;

    @Column(name = "update_status", nullable = false)
    private Boolean updateStatus;
}

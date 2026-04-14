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
@Table(name = "meeting_config")
public class MeetingConfig {

    @Id
    @Column(name = "config_id", length = 30)
    private String configId;

    @Column(name = "form_id", nullable = false, length = 30, unique = true)
    private String formId;

    @Column(name = "synopsis_required", nullable = false, columnDefinition = "integer default 0")
    private Integer synopsisRequired;

    @Column(name = "progress1_required", nullable = false, columnDefinition = "integer default 0")
    private Integer progress1Required;

    @Column(name = "progress2_required", nullable = false, columnDefinition = "integer default 0")
    private Integer progress2Required;

    @Column(name = "final_required", nullable = false, columnDefinition = "integer default 0")
    private Integer finalRequired;
}

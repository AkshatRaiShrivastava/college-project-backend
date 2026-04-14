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
@Table(name = "progress2")
public class Progress2Submission extends AbstractSubmission {

    @Id
    @Column(name = "progress2_id", length = 30)
    private String progress2Id;
}

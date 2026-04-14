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
@Table(name = "final_submission")
public class FinalSubmission extends AbstractSubmission {

    @Id
    @Column(name = "final_id", length = 30)
    private String finalId;
}

package com.akshat.college_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorImportResponse {
    private int total;
    private int inserted;
    private int failed;
    private List<ImportError> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private int row;
        private String message;
    }
}

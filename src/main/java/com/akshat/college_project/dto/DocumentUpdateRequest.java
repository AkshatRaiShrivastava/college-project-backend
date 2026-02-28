package com.akshat.college_project.dto;

import java.util.List;

public record DocumentUpdateRequest(
        String projectId,
        List<String> synopsisIds,
        List<String> progress1Ids,
        List<String> progress2Ids,
        List<String> finalIds
) {
}

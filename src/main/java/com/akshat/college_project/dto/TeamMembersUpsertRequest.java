package com.akshat.college_project.dto;

import java.util.List;

public record TeamMembersUpsertRequest(
        List<String> joinMemberIds,
        List<String> notJoinMemberIds
) {
}

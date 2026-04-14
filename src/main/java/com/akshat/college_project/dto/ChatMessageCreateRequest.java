package com.akshat.college_project.dto;

public record ChatMessageCreateRequest(
    String projectId,
    String senderId,
    String senderRole,
    String senderName,
    String messageText
) {}

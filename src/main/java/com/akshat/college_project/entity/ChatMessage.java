package com.akshat.college_project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_messages")
@Data
public class ChatMessage {

    @Id
    @Column(name = "message_id", updatable = false, nullable = false)
    private String messageId;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_role", nullable = false)
    private String senderRole; // "STUDENT" or "SUPERVISOR"

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
    private String messageText;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}

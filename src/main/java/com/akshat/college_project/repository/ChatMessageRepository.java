package com.akshat.college_project.repository;

import com.akshat.college_project.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByProjectIdOrderByCreatedAtAsc(String projectId);
}

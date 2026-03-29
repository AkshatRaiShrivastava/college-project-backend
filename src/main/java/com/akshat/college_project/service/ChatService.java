package com.akshat.college_project.service;

import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.dto.ChatMessageCreateRequest;
import com.akshat.college_project.entity.ChatMessage;
import com.akshat.college_project.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    
    public ChatService(ChatMessageRepository chatMessageRepository, org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatMessage createMessage(ChatMessageCreateRequest request) {
        ChatMessage message = new ChatMessage();
        message.setMessageId(IdGenerator.generate("msg_"));
        message.setProjectId(request.projectId());
        message.setSenderId(request.senderId());
        message.setSenderRole(request.senderRole());
        message.setSenderName(request.senderName());
        message.setMessageText(request.messageText());
        
        ChatMessage saved = chatMessageRepository.save(message);
        
        // Broadcast the real persisted entity to all active subscribers via WebSocket topic
        messagingTemplate.convertAndSend("/topic/project/" + request.projectId(), saved);
        
        return saved;
    }

    public List<ChatMessage> getMessagesByProject(String projectId) {
        return chatMessageRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
    }
}

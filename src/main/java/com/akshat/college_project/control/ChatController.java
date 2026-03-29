package com.akshat.college_project.control;

import com.akshat.college_project.dto.ChatMessageCreateRequest;
import com.akshat.college_project.entity.ChatMessage;
import com.akshat.college_project.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Allows React to connect seamlessly
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatMessage> createMessage(@RequestBody ChatMessageCreateRequest request) {
        return ResponseEntity.ok(chatService.createMessage(request));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ChatMessage>> getMessagesByProject(@PathVariable String projectId) {
        return ResponseEntity.ok(chatService.getMessagesByProject(projectId));
    }
}

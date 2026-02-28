package com.akshat.college_project.controller;

import com.akshat.college_project.dto.DocumentCreateRequest;
import com.akshat.college_project.dto.DocumentUpdateRequest;
import com.akshat.college_project.entity.Document;
import com.akshat.college_project.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<Document> create(@Valid @RequestBody DocumentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.create(request));
    }

    @GetMapping("/{documentId}")
    public Document get(@PathVariable String documentId) {
        return documentService.get(documentId);
    }

    @GetMapping("/by-project/{projectId}")
    public Document getByProject(@PathVariable String projectId) {
        return documentService.getByProjectId(projectId);
    }

    @GetMapping
    public List<Document> getAll() {
        return documentService.getAll();
    }

    @PutMapping("/{documentId}")
    public Document update(@PathVariable String documentId, @RequestBody DocumentUpdateRequest request) {
        return documentService.update(documentId, request);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> delete(@PathVariable String documentId) {
        documentService.delete(documentId);
        return ResponseEntity.noContent().build();
    }
}

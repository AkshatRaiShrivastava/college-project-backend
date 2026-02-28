package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.JsonArrayCodec;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.DocumentCreateRequest;
import com.akshat.college_project.dto.DocumentUpdateRequest;
import com.akshat.college_project.entity.Document;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.enums.StageStatus;
import com.akshat.college_project.repository.DocumentRepository;
import com.akshat.college_project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProjectRepository projectRepository;
    private final ReferenceValidator referenceValidator;
    private final JsonArrayCodec jsonArrayCodec;

    public DocumentService(
            DocumentRepository documentRepository,
            ProjectRepository projectRepository,
            ReferenceValidator referenceValidator,
            JsonArrayCodec jsonArrayCodec
    ) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.referenceValidator = referenceValidator;
        this.jsonArrayCodec = jsonArrayCodec;
    }

    @Transactional
    public Document create(DocumentCreateRequest request) {
        referenceValidator.requireProject(request.projectId());
        if (documentRepository.findByProjectId(request.projectId()).isPresent()) {
            throw new BadRequestException("Document already exists for project: " + request.projectId());
        }

        Document document = new Document();
        document.setDocumentId(IdGenerator.generate("doc_"));
        document.setProjectId(request.projectId());
        Document saved = documentRepository.save(document);

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + request.projectId()));
        project.setDocumentId(saved.getDocumentId());
        projectRepository.save(project);
        return saved;
    }

    public Document get(String documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));
    }

    public List<Document> getAll() {
        return documentRepository.findAll();
    }

    public Document getByProjectId(String projectId) {
        return documentRepository.findByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found for project: " + projectId));
    }

    @Transactional
    public Document update(String documentId, DocumentUpdateRequest request) {
        Document document = get(documentId);

        if (request.projectId() != null && !request.projectId().equals(document.getProjectId())) {
            referenceValidator.requireProject(request.projectId());
            document.setProjectId(request.projectId());
        }

        if (request.synopsisIds() != null) {
            document.setSynopsisId(jsonArrayCodec.toJson(request.synopsisIds()));
        }
        if (request.progress1Ids() != null) {
            document.setProgress1Id(jsonArrayCodec.toJson(request.progress1Ids()));
        }
        if (request.progress2Ids() != null) {
            document.setProgress2Id(jsonArrayCodec.toJson(request.progress2Ids()));
        }
        if (request.finalIds() != null) {
            document.setFinalId(jsonArrayCodec.toJson(request.finalIds()));
        }

        return documentRepository.save(document);
    }

    @Transactional
    public void appendSubmissionId(String documentId, StageStatus stage, String submissionId) {
        Document document = get(documentId);
        switch (stage) {
            case SYNOPSIS -> {
                List<String> values = jsonArrayCodec.fromJson(document.getSynopsisId());
                values.add(submissionId);
                document.setSynopsisId(jsonArrayCodec.toJson(values));
            }
            case PROGRESS1 -> {
                List<String> values = jsonArrayCodec.fromJson(document.getProgress1Id());
                values.add(submissionId);
                document.setProgress1Id(jsonArrayCodec.toJson(values));
            }
            case PROGRESS2 -> {
                List<String> values = jsonArrayCodec.fromJson(document.getProgress2Id());
                values.add(submissionId);
                document.setProgress2Id(jsonArrayCodec.toJson(values));
            }
            case FINAL -> {
                List<String> values = jsonArrayCodec.fromJson(document.getFinalId());
                values.add(submissionId);
                document.setFinalId(jsonArrayCodec.toJson(values));
            }
            default -> throw new BadRequestException("Unsupported stage: " + stage);
        }
        documentRepository.save(document);
    }

    @Transactional
    public void removeSubmissionId(String documentId, StageStatus stage, String submissionId) {
        Document document = get(documentId);
        switch (stage) {
            case SYNOPSIS -> {
                List<String> values = jsonArrayCodec.fromJson(document.getSynopsisId());
                values.remove(submissionId);
                document.setSynopsisId(jsonArrayCodec.toJson(values));
            }
            case PROGRESS1 -> {
                List<String> values = jsonArrayCodec.fromJson(document.getProgress1Id());
                values.remove(submissionId);
                document.setProgress1Id(jsonArrayCodec.toJson(values));
            }
            case PROGRESS2 -> {
                List<String> values = jsonArrayCodec.fromJson(document.getProgress2Id());
                values.remove(submissionId);
                document.setProgress2Id(jsonArrayCodec.toJson(values));
            }
            case FINAL -> {
                List<String> values = jsonArrayCodec.fromJson(document.getFinalId());
                values.remove(submissionId);
                document.setFinalId(jsonArrayCodec.toJson(values));
            }
            default -> throw new BadRequestException("Unsupported stage: " + stage);
        }
        documentRepository.save(document);
    }

    @Transactional
    public void delete(String documentId) {
        Document document = get(documentId);
        projectRepository.findByDocumentId(documentId).ifPresent(project -> {
            project.setDocumentId(null);
            projectRepository.save(project);
        });
        documentRepository.delete(document);
    }
}

package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.FormAttachment;
import com.akshat.college_project.dto.FormCreateRequest;
import com.akshat.college_project.dto.FormUpdateRequest;
import com.akshat.college_project.entity.Form;
import com.akshat.college_project.repository.FormRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FormService {

    private final FormRepository formRepository;
    private final ReferenceValidator referenceValidator;
    private final OneDriveStorageService oneDriveStorageService;
    private final ObjectMapper objectMapper;

    public FormService(
            FormRepository formRepository,
            ReferenceValidator referenceValidator,
            OneDriveStorageService oneDriveStorageService,
            ObjectMapper objectMapper
    ) {
        this.formRepository = formRepository;
        this.referenceValidator = referenceValidator;
        this.oneDriveStorageService = oneDriveStorageService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Form create(FormCreateRequest request) {
        referenceValidator.requireAdmin(request.createdBy());

        Form form = new Form();
        form.setFormId(IdGenerator.generate("frm_"));
        form.setAccessBranch(request.accessBranch());
        form.setAccessBatch(request.accessBatch());
        form.setJsonOfFields(request.jsonOfFields());
        form.setReferenceFilesJson("[]");
        form.setCreatedBy(request.createdBy());
        return formRepository.save(form);
    }

    public Form get(String formId) {
        return formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found: " + formId));
    }

    public List<Form> getAll() {
        return formRepository.findAll();
    }

    public List<FormAttachment> getAttachments(String formId) {
        Form form = get(formId);
        return readAttachments(form.getReferenceFilesJson());
    }

    @Transactional
    public FormAttachment uploadAttachment(String formId, MultipartFile file, String uploadedBy) throws IOException {
        if (uploadedBy != null && !uploadedBy.isBlank()) {
            referenceValidator.requireAdmin(uploadedBy);
        }

        Form form = get(formId);
        String fileUrl = oneDriveStorageService.uploadFile(file, formId, "reference-files");
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.bin";

        FormAttachment attachment = new FormAttachment(
                IdGenerator.generate("ref_"),
                fileName,
                fileUrl,
                uploadedBy,
            Instant.now(),
            "UPLOAD"
        );

        List<FormAttachment> attachments = new ArrayList<>(readAttachments(form.getReferenceFilesJson()));
        attachments.add(attachment);
        form.setReferenceFilesJson(writeAttachments(attachments));
        formRepository.save(form);
        return attachment;
    }

    @Transactional
    public FormAttachment addAttachmentLink(String formId, String fileName, String fileUrl, String uploadedBy) {
        if (uploadedBy != null && !uploadedBy.isBlank()) {
            referenceValidator.requireAdmin(uploadedBy);
        }

        if (fileUrl == null || fileUrl.isBlank() || !fileUrl.startsWith("http")) {
            throw new BadRequestException("Invalid file URL");
        }

        Form form = get(formId);
        FormAttachment attachment = new FormAttachment(
                IdGenerator.generate("ref_"),
                fileName,
                fileUrl,
                uploadedBy,
                Instant.now(),
                "LINK"
        );

        List<FormAttachment> attachments = new ArrayList<>(readAttachments(form.getReferenceFilesJson()));
        attachments.add(attachment);
        form.setReferenceFilesJson(writeAttachments(attachments));
        formRepository.save(form);
        return attachment;
    }

    public Form update(String formId, FormUpdateRequest request) {
        Form form = get(formId);

        if (request.accessBranch() != null) {
            form.setAccessBranch(request.accessBranch());
        }
        if (request.accessBatch() != null) {
            form.setAccessBatch(request.accessBatch());
        }
        if (request.jsonOfFields() != null) {
            form.setJsonOfFields(request.jsonOfFields());
        }
        if (request.createdBy() != null) {
            referenceValidator.requireAdmin(request.createdBy());
            form.setCreatedBy(request.createdBy());
        }

        return formRepository.save(form);
    }

    public void delete(String formId) {
        Form form = get(formId);
        formRepository.delete(form);
    }

    private List<FormAttachment> readAttachments(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<FormAttachment>>() { });
        } catch (Exception ex) {
            throw new BadRequestException("Stored form attachments are invalid");
        }
    }

    private String writeAttachments(List<FormAttachment> attachments) {
        try {
            return objectMapper.writeValueAsString(attachments);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize form attachments", ex);
        }
    }
}

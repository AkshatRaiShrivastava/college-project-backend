package com.akshat.college_project.controller;

import com.akshat.college_project.dto.FormAttachment;
import com.akshat.college_project.dto.FormAttachmentLinkRequest;
import com.akshat.college_project.dto.FormCreateRequest;
import com.akshat.college_project.dto.FormUpdateRequest;
import com.akshat.college_project.entity.Form;
import com.akshat.college_project.service.FormService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping
    public ResponseEntity<Form> create(@Valid @RequestBody FormCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(formService.create(request));
    }

    @GetMapping("/{formId}")
    public Form get(@PathVariable String formId) {
        return formService.get(formId);
    }

    @GetMapping
    public List<Form> getAll() {
        return formService.getAll();
    }

    @GetMapping("/{formId}/attachments")
    public List<FormAttachment> getAttachments(@PathVariable String formId) {
        return formService.getAttachments(formId);
    }

    @PostMapping(value = "/{formId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FormAttachment> uploadAttachment(
            @PathVariable String formId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "stage", required = false) String stage,
            @RequestParam(value = "uploadedBy", required = false) String uploadedBy
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(formService.uploadAttachment(formId, file, uploadedBy, stage));
    }

        @PostMapping("/{formId}/attachments/link")
        public ResponseEntity<FormAttachment> addAttachmentLink(
            @PathVariable String formId,
            @Valid @RequestBody FormAttachmentLinkRequest request
        ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                formService.addAttachmentLink(formId, request.fileName(), request.fileUrl(), request.stage(), request.uploadedBy())
        );
        }

    @PutMapping("/{formId}")
    public Form update(@PathVariable String formId, @RequestBody FormUpdateRequest request) {
        return formService.update(formId, request);
    }

    @DeleteMapping("/{formId}")
    public ResponseEntity<Void> delete(@PathVariable String formId) {
        formService.delete(formId);
        return ResponseEntity.noContent().build();
    }
}

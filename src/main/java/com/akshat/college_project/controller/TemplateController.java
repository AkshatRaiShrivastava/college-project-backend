package com.akshat.college_project.controller;

import com.akshat.college_project.dto.TemplateLinkRequest;
import com.akshat.college_project.entity.Template;
import com.akshat.college_project.service.TemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<Template> getTemplatesByFormId(@RequestParam("form_id") String formId) {
        return templateService.getTemplatesByFormId(formId);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Template> uploadTemplate(
            @RequestParam("formId") String formId,
            @RequestParam("stageId") String stageId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        Template template = templateService.uploadTemplate(formId, stageId, name, description, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(template);
    }

    @PostMapping("/link")
    public ResponseEntity<Template> addTemplateLink(@Valid @RequestBody TemplateLinkRequest request) {
        Template template = templateService.addTemplateLink(
                request.formId(),
                request.stageId(),
                request.name(),
                request.description(),
                request.fileUrl()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(template);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}

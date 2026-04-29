package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.entity.Template;
import com.akshat.college_project.repository.TemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final OneDriveStorageService oneDriveStorageService;

    public TemplateService(TemplateRepository templateRepository, OneDriveStorageService oneDriveStorageService) {
        this.templateRepository = templateRepository;
        this.oneDriveStorageService = oneDriveStorageService;
    }

    public List<Template> getTemplatesByFormId(String formId) {
        return templateRepository.findByFormId(formId);
    }

    @Transactional
    public Template uploadTemplate(String formId, String stageId, String name, String description, MultipartFile file) throws IOException {
        String fileUrl = oneDriveStorageService.uploadFile(file, formId, "templates/" + stageId);
        
        String filename = file.getOriginalFilename();
        String type = "OTHER";
        if (filename != null && filename.contains(".")) {
            type = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
        }

        Template template = new Template();
        template.setId(IdGenerator.generate("tpl_"));
        template.setFormId(formId);
        template.setStageId(stageId);
        template.setName(name);
        template.setDescription(description);
        template.setType(type);
        template.setSourceType("UPLOAD");
        template.setFileUrl(fileUrl);

        return templateRepository.save(template);
    }

    @Transactional
    public Template addTemplateLink(String formId, String stageId, String name, String description, String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank() || !fileUrl.startsWith("http")) {
            throw new BadRequestException("Invalid file URL");
        }

        Template template = new Template();
        template.setId(IdGenerator.generate("tpl_"));
        template.setFormId(formId);
        template.setStageId(stageId);
        template.setName(name);
        template.setDescription(description);
        template.setType("DRIVE_LINK");
        template.setSourceType("LINK");
        template.setFileUrl(fileUrl);

        return templateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(String id) {
        templateRepository.deleteById(id);
    }
}

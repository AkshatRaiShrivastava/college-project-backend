package com.akshat.college_project.service;

import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.FormCreateRequest;
import com.akshat.college_project.dto.FormUpdateRequest;
import com.akshat.college_project.entity.Form;
import com.akshat.college_project.repository.FormRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormService {

    private final FormRepository formRepository;
    private final ReferenceValidator referenceValidator;

    public FormService(FormRepository formRepository, ReferenceValidator referenceValidator) {
        this.formRepository = formRepository;
        this.referenceValidator = referenceValidator;
    }

    public Form create(FormCreateRequest request) {
        referenceValidator.requireAdmin(request.createdBy());

        Form form = new Form();
        form.setFormId(IdGenerator.generate("frm_"));
        form.setAccessBranch(request.accessBranch());
        form.setAccessBatch(request.accessBatch());
        form.setJsonOfFields(request.jsonOfFields());
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
}

package com.akshat.college_project.controller;

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
import org.springframework.web.bind.annotation.RestController;

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

package com.akshat.college_project.service;

import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.repository.AdminRepository;
import com.akshat.college_project.repository.DocumentRepository;
import com.akshat.college_project.repository.FormRepository;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.repository.StudentRepository;
import com.akshat.college_project.repository.SupervisorRepository;
import com.akshat.college_project.repository.TeamRepository;
import org.springframework.stereotype.Component;

@Component
public class ReferenceValidator {

    private final StudentRepository studentRepository;
    private final SupervisorRepository supervisorRepository;
    private final AdminRepository adminRepository;
    private final FormRepository formRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final DocumentRepository documentRepository;

    public ReferenceValidator(
            StudentRepository studentRepository,
            SupervisorRepository supervisorRepository,
            AdminRepository adminRepository,
            FormRepository formRepository,
            TeamRepository teamRepository,
            ProjectRepository projectRepository,
            DocumentRepository documentRepository
    ) {
        this.studentRepository = studentRepository;
        this.supervisorRepository = supervisorRepository;
        this.adminRepository = adminRepository;
        this.formRepository = formRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.documentRepository = documentRepository;
    }

    public void requireStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found: " + studentId);
        }
    }

    public void requireSupervisor(String supervisorId) {
        if (!supervisorRepository.existsById(supervisorId)) {
            throw new ResourceNotFoundException("Supervisor not found: " + supervisorId);
        }
    }

    public void requireAdmin(String adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new ResourceNotFoundException("Admin not found: " + adminId);
        }
    }

    public void requireForm(String formId) {
        if (!formRepository.existsById(formId)) {
            throw new ResourceNotFoundException("Form not found: " + formId);
        }
    }

    public void requireTeam(String teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found: " + teamId);
        }
    }

    public void requireProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found: " + projectId);
        }
    }

    public void requireDocument(String documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new ResourceNotFoundException("Document not found: " + documentId);
        }
    }
}

package com.akshat.college_project.service;

import com.akshat.college_project.common.BadRequestException;
import com.akshat.college_project.common.IdGenerator;
import com.akshat.college_project.common.ResourceNotFoundException;
import com.akshat.college_project.dto.ProjectCreateRequest;
import com.akshat.college_project.dto.ProjectUpdateRequest;
import com.akshat.college_project.entity.Document;
import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.Team;
import com.akshat.college_project.repository.DocumentRepository;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final DocumentRepository documentRepository;
    private final ReferenceValidator referenceValidator;

    public ProjectService(
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            DocumentRepository documentRepository,
            ReferenceValidator referenceValidator
    ) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.documentRepository = documentRepository;
        this.referenceValidator = referenceValidator;
    }

    @Transactional
    public Project create(ProjectCreateRequest request) {
        referenceValidator.requireTeam(request.teamId());
        if (projectRepository.existsByTeamId(request.teamId())) {
            throw new BadRequestException("A project already exists for team: " + request.teamId());
        }
        referenceValidator.requireForm(request.formId());

        if (request.supervisorId() != null) {
            referenceValidator.requireSupervisor(request.supervisorId());
        }
        if (request.supervisorAssignedBy() != null) {
            referenceValidator.requireAdmin(request.supervisorAssignedBy());
        }

        Project project = new Project();
        project.setProjectId(IdGenerator.generate("prj_"));
        project.setTeamId(request.teamId());
        project.setFormId(request.formId());
        project.setSupervisorId(request.supervisorId());
        project.setProjectTitle(request.projectTitle());
        project.setProjectDescription(request.projectDescription());
        project.setStatus(request.status());
        project.setStageStatus(request.stageStatus());
        project.setSynopsisScore(request.synopsisScore());
        project.setProgress1Score(request.progress1Score());
        project.setProgress2Score(request.progress2Score());
        project.setFinalScore(request.finalScore());
        project.setAdminFinalScore(request.adminFinalScore());
        project.setStagesScoreOutOf(request.stagesScoreOutOf());
        project.setAdminFinalscoreOutOf(request.adminFinalscoreOutOf());
        project.setOldSupervisor(request.oldSupervisor());
        project.setSupervisorAssignedBy(request.supervisorAssignedBy());

        Project savedProject = projectRepository.save(project);

        Team team = teamRepository.findById(savedProject.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + savedProject.getTeamId()));
        team.setProjectId(savedProject.getProjectId());
        teamRepository.save(team);

        if (request.documentId() != null) {
            Document existingDocument = documentRepository.findById(request.documentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + request.documentId()));
            existingDocument.setProjectId(savedProject.getProjectId());
            documentRepository.save(existingDocument);
            savedProject.setDocumentId(existingDocument.getDocumentId());
        } else {
            Document document = new Document();
            document.setDocumentId(IdGenerator.generate("doc_"));
            document.setProjectId(savedProject.getProjectId());
            Document createdDocument = documentRepository.save(document);
            savedProject.setDocumentId(createdDocument.getDocumentId());
        }

        return projectRepository.save(savedProject);
    }

    public Project get(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
    }

    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    public Project getByTeamId(String teamId) {
        return projectRepository.findByTeamId(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found for team: " + teamId));
    }

    @Transactional
    public Project update(String projectId, ProjectUpdateRequest request) {
        Project project = get(projectId);
        String previousTeamId = project.getTeamId();

        if (request.teamId() != null && !request.teamId().equals(project.getTeamId())) {
            referenceValidator.requireTeam(request.teamId());
            if (projectRepository.existsByTeamId(request.teamId())) {
                throw new BadRequestException("A project already exists for team: " + request.teamId());
            }
            project.setTeamId(request.teamId());
        }

        if (request.formId() != null) {
            referenceValidator.requireForm(request.formId());
            project.setFormId(request.formId());
        }

        if (request.supervisorId() != null) {
            if (project.getSupervisorId() != null && !project.getSupervisorId().equals(request.supervisorId())) {
                project.setOldSupervisor(project.getSupervisorId());
            }
            referenceValidator.requireSupervisor(request.supervisorId());
            project.setSupervisorId(request.supervisorId());
        }

        if (request.supervisorAssignedBy() != null) {
            referenceValidator.requireAdmin(request.supervisorAssignedBy());
            project.setSupervisorAssignedBy(request.supervisorAssignedBy());
        }

        if (request.projectTitle() != null) {
            project.setProjectTitle(request.projectTitle());
        }
        if (request.projectDescription() != null) {
            project.setProjectDescription(request.projectDescription());
        }
        if (request.status() != null) {
            project.setStatus(request.status());
        }
        if (request.stageStatus() != null) {
            project.setStageStatus(request.stageStatus());
        }
        if (request.synopsisScore() != null) {
            project.setSynopsisScore(request.synopsisScore());
        }
        if (request.progress1Score() != null) {
            project.setProgress1Score(request.progress1Score());
        }
        if (request.progress2Score() != null) {
            project.setProgress2Score(request.progress2Score());
        }
        if (request.finalScore() != null) {
            project.setFinalScore(request.finalScore());
        }
        if (request.adminFinalScore() != null) {
            project.setAdminFinalScore(request.adminFinalScore());
        }
        if (request.stagesScoreOutOf() != null) {
            project.setStagesScoreOutOf(request.stagesScoreOutOf());
        }
        if (request.adminFinalscoreOutOf() != null) {
            project.setAdminFinalscoreOutOf(request.adminFinalscoreOutOf());
        }
        if (request.oldSupervisor() != null) {
            project.setOldSupervisor(request.oldSupervisor());
        }

        if (request.documentId() != null) {
            Document document = documentRepository.findById(request.documentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + request.documentId()));
            document.setProjectId(projectId);
            documentRepository.save(document);
            project.setDocumentId(document.getDocumentId());
        }

        Project savedProject = projectRepository.save(project);

        if (!previousTeamId.equals(savedProject.getTeamId())) {
            teamRepository.findById(previousTeamId).ifPresent(previousTeam -> {
                previousTeam.setProjectId(null);
                teamRepository.save(previousTeam);
            });
        }

        Team currentTeam = teamRepository.findById(savedProject.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + savedProject.getTeamId()));
        currentTeam.setProjectId(savedProject.getProjectId());
        teamRepository.save(currentTeam);

        return savedProject;
    }

    @Transactional
    public void delete(String projectId) {
        Project project = get(projectId);
        teamRepository.findById(project.getTeamId()).ifPresent(team -> {
            team.setProjectId(null);
            teamRepository.save(team);
        });
        projectRepository.delete(project);
    }
}

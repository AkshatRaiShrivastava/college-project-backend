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

import com.akshat.college_project.repository.SupervisorRepository;
import com.akshat.college_project.entity.Supervisor;
import com.akshat.college_project.entity.SupervisorHistory;
import com.akshat.college_project.repository.SupervisorHistoryRepository;
import com.akshat.college_project.repository.AdminRepository;
import com.akshat.college_project.dto.SupervisorAssignRequest;
import com.akshat.college_project.entity.Admin;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final DocumentRepository documentRepository;
    private final SupervisorRepository supervisorRepository;
    private final ReferenceValidator referenceValidator;
    private final MailService mailService;
    private final SupervisorHistoryRepository supervisorHistoryRepository;
    private final NotificationService notificationService;
    private final AdminRepository adminRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            DocumentRepository documentRepository,
            SupervisorRepository supervisorRepository,
            ReferenceValidator referenceValidator,
            MailService mailService,
            SupervisorHistoryRepository supervisorHistoryRepository,
            NotificationService notificationService,
            AdminRepository adminRepository
    ) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.documentRepository = documentRepository;
        this.supervisorRepository = supervisorRepository;
        this.referenceValidator = referenceValidator;
        this.mailService = mailService;
        this.supervisorHistoryRepository = supervisorHistoryRepository;
        this.notificationService = notificationService;
        this.adminRepository = adminRepository;
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

        boolean triggerEmail = false;

        if (request.supervisorId() != null) {
            if (project.getSupervisorId() != null && !project.getSupervisorId().equals(request.supervisorId())) {
                project.setOldSupervisor(project.getSupervisorId());
            }
            referenceValidator.requireSupervisor(request.supervisorId());
            if (project.getSupervisorId() == null || !project.getSupervisorId().equals(request.supervisorId())) {
                triggerEmail = true;
            }
            project.setSupervisorId(request.supervisorId());
        }

        if (request.supervisorAssignedBy() != null) {
            referenceValidator.requireAdmin(request.supervisorAssignedBy());
            project.setSupervisorAssignedBy(request.supervisorAssignedBy());
        }

        if (request.projectTitle() != null) {
            project.setProjectTitle(request.projectTitle());
        }

        if (triggerEmail) {
            final String finalTitle = project.getProjectTitle();
            supervisorRepository.findById(request.supervisorId()).ifPresent(sup -> {
                try {
                    mailService.sendAssignmentMail(sup.getMail(), finalTitle != null ? finalTitle : "New Project", "Admin");
                } catch (Exception e) {
                    System.err.println("Failed to send assignment email: " + e.getMessage());
                }
            });
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

    @Transactional
    public Project assignSupervisor(String projectId, SupervisorAssignRequest request) {
        Project project = get(projectId);
        referenceValidator.requireSupervisor(request.supervisorId());
        referenceValidator.requireAdmin(request.adminId());

        String oldSupervisorId = project.getSupervisorId();
        String newSupervisorId = request.supervisorId();

        if (newSupervisorId.equals(oldSupervisorId)) {
            throw new BadRequestException("Supervisor is already assigned to this project.");
        }

        if (oldSupervisorId != null && (request.reason() == null || request.reason().isBlank())) {
            throw new BadRequestException("Reason is required when changing an existing supervisor.");
        }

        project.setOldSupervisor(oldSupervisorId);
        project.setSupervisorId(newSupervisorId);
        project.setSupervisorAssignedBy(request.adminId());
        
        Project savedProject = projectRepository.save(project);

        SupervisorHistory history = new SupervisorHistory();
        history.setId(IdGenerator.generate("suph_"));
        history.setProjectId(projectId);
        history.setOldSupervisorId(oldSupervisorId);
        history.setNewSupervisorId(newSupervisorId);
        history.setChangedBy(request.adminId());
        history.setReason(request.reason());
        supervisorHistoryRepository.save(history);

        Admin admin = adminRepository.findById(request.adminId()).orElse(null);
        String adminName = admin != null ? admin.getName() : "System Admin";
        
        Supervisor newSupervisor = supervisorRepository.findById(newSupervisorId).orElse(null);
        String newSuperName = newSupervisor != null ? newSupervisor.getName() : newSupervisorId;

        // Notify New Supervisor
        notificationService.createNotification(newSupervisorId, "supervisor",
                "You have been assigned to Project: " + project.getProjectTitle());
        if (newSupervisor != null) {
            mailService.sendAssignmentMail(newSupervisor.getMail(), project.getProjectTitle(), adminName);
        }

        // Notify Old Supervisor
        if (oldSupervisorId != null) {
            notificationService.createNotification(oldSupervisorId, "supervisor",
                    "You have been unassigned from Project: " + project.getProjectTitle() + ". Reason: " + request.reason());
            Supervisor oldSupervisor = supervisorRepository.findById(oldSupervisorId).orElse(null);
            if (oldSupervisor != null) {
                mailService.sendUnassignmentMail(oldSupervisor.getMail(), project.getProjectTitle(), request.reason());
            }
        }

        // Notify Students
        try {
            Team team = teamRepository.findById(project.getTeamId()).orElse(null);
            if (team != null && team.getTeamMemberArray() != null) {
                List<String> studentIds = new java.util.ArrayList<>();
                String arrayStr = team.getTeamMemberArray();
                if (arrayStr != null && arrayStr.startsWith("[") && arrayStr.endsWith("]")) {
                    String inner = arrayStr.substring(1, arrayStr.length() - 1);
                    if (!inner.isEmpty()) {
                        String[] parts = inner.split(",");
                        for (String part : parts) {
                            studentIds.add(part.trim().replace("\"", ""));
                        }
                    }
                }
                for (String studentId : studentIds) {
                    if (oldSupervisorId == null) {
                        notificationService.createNotification(studentId, "student",
                                "Supervisor " + newSuperName + " has been assigned to your project.");
                    } else {
                        Supervisor oldSupervisor = supervisorRepository.findById(oldSupervisorId).orElse(null);
                        String oldSuperName = oldSupervisor != null ? oldSupervisor.getName() : oldSupervisorId;
                        notificationService.createNotification(studentId, "student",
                                "Supervisor changed from " + oldSuperName + " to " + newSuperName + " by Admin.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse team members for notifications: " + e.getMessage());
        }

        return savedProject;
    }

    public List<SupervisorHistory> getSupervisorHistory(String projectId) {
        return supervisorHistoryRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }
}

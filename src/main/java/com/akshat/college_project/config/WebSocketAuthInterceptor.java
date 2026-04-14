package com.akshat.college_project.config;

import com.akshat.college_project.entity.Project;
import com.akshat.college_project.entity.TeamMembers;
import com.akshat.college_project.repository.ProjectRepository;
import com.akshat.college_project.repository.TeamMembersRepository;
import com.akshat.college_project.repository.TeamRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TeamMembersRepository teamMembersRepository;

    public WebSocketAuthInterceptor(ProjectRepository projectRepository, TeamRepository teamRepository, TeamMembersRepository teamMembersRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.teamMembersRepository = teamMembersRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            String userId = accessor.getFirstNativeHeader("userId");
            String userRole = accessor.getFirstNativeHeader("userRole");

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                if (userId == null || userId.isEmpty()) {
                    throw new IllegalArgumentException("Unauthorized: Missing userId header during handshake");
                }
                accessor.getSessionAttributes().put("userId", userId);
                accessor.getSessionAttributes().put("userRole", userRole != null ? userRole : "STUDENT");
            } 
            else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                String destination = accessor.getDestination();
                String sessionUserId = (String) accessor.getSessionAttributes().get("userId");
                String sessionUserRole = (String) accessor.getSessionAttributes().get("userRole");

                if (sessionUserId == null) {
                    throw new IllegalArgumentException("Unauthorized: No active session");
                }

                if (destination != null && destination.startsWith("/topic/project/")) {
                    String projectId = destination.substring("/topic/project/".length());
                    validateUserCanAccessProjectPhase(sessionUserId, sessionUserRole, projectId);
                }
            }
        }
        return message;
    }

    private void validateUserCanAccessProjectPhase(String userId, String role, String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        
        if ("ADMIN".equals(role)) {
            return; // Admins have master access
        }
        
        if ("SUPERVISOR".equals(role)) {
            if (!userId.equals(project.getSupervisorId())) {
                throw new IllegalArgumentException("Forbidden: Supervisor is not assigned to this project");
            }
            return;
        }

        // Student Check
        String teamId = project.getTeamId();
        if (teamId == null) {
            throw new IllegalArgumentException("Forbidden: Project has no active team");
        }
        
        com.akshat.college_project.entity.Team team = teamRepository.findById(teamId).orElseThrow(() -> new IllegalArgumentException("Forbidden: Team not found"));
        boolean isLeader = userId.equals(team.getLeaderId());
        if (isLeader) return;

        // Check if member
        TeamMembers tm = teamMembersRepository.findByTeamId(teamId).orElseThrow(() -> new IllegalArgumentException("Team Members not found"));
        if (tm.getJoinMemberArray() == null || !tm.getJoinMemberArray().contains(userId)) {
            throw new IllegalArgumentException("Forbidden: Student is not part of this team's communication hub");
        }
    }
}

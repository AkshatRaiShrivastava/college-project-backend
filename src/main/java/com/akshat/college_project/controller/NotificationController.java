package com.akshat.college_project.controller;

import com.akshat.college_project.entity.Notification;
import com.akshat.college_project.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable String userId) {
        return notificationService.getNotificationsForUser(userId);
    }

    @GetMapping("/{userId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable String userId) {
        return notificationService.getUnreadNotificationsForUser(userId);
    }

    @GetMapping("/{userId}/unread-count")
    public Map<String, Integer> getUnreadCount(@PathVariable String userId) {
        return Map.of("count", notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}

package com.akshat.college_project.controller;

import com.akshat.college_project.dto.LeaderboardCreateRequest;
import com.akshat.college_project.dto.LeaderboardUpdateRequest;
import com.akshat.college_project.entity.Leaderboard;
import com.akshat.college_project.service.LeaderboardService;
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
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping
    public ResponseEntity<Leaderboard> create(@Valid @RequestBody LeaderboardCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaderboardService.create(request));
    }

    @GetMapping("/{projectId}")
    public Leaderboard get(@PathVariable String projectId) {
        return leaderboardService.get(projectId);
    }

    @GetMapping
    public List<Leaderboard> getAll() {
        return leaderboardService.getAll();
    }

    @PutMapping("/{projectId}")
    public Leaderboard update(@PathVariable String projectId, @RequestBody LeaderboardUpdateRequest request) {
        return leaderboardService.update(projectId, request);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable String projectId) {
        leaderboardService.delete(projectId);
        return ResponseEntity.noContent().build();
    }
}

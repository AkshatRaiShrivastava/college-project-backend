package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaderboardRepository extends JpaRepository<Leaderboard, String> {

    List<Leaderboard> findAllByOrderByRankAsc();
}

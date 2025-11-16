package org.example.goalmanager.service;

import org.example.goalmanager.dto.goal.CreateGoalRequest;
import org.example.goalmanager.dto.goal.GoalStatistics;
import org.example.goalmanager.dto.goal.UpdateGoalRequest;
import org.example.goalmanager.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface GoalService {

    Goal createGoal(CreateGoalRequest request, Long userId);

    Goal getGoalById(Long id);

    Page<Goal> getUserGoals(Long userId, Pageable pageable);

    Goal updateGoal(Long goalId, UpdateGoalRequest request);

    void deleteGoal(Long goalId);

    Goal updateProgress(Long goalId, Integer progress);

    Goal updateDeadline(Long goalId, LocalDateTime newDeadline);

    GoalStatistics getGoalStatistics(Long userId);

    List<Goal> getGoalsWithApproachingDeadlines();
}


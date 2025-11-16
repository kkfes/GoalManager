package org.example.goalmanager.service.impl;

import org.example.goalmanager.dto.goal.CreateGoalRequest;
import org.example.goalmanager.dto.goal.GoalStatistics;
import org.example.goalmanager.dto.goal.UpdateGoalRequest;
import org.example.goalmanager.entity.Goal;
import org.example.goalmanager.entity.GoalStatus;
import org.example.goalmanager.entity.User;
import org.example.goalmanager.exception.NotFoundException;
import org.example.goalmanager.repository.GoalRepository;
import org.example.goalmanager.repository.UserRepository;
import org.example.goalmanager.service.GoalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalServiceImpl(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Goal createGoal(CreateGoalRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        Goal goal = Goal.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .targetDate(request.getTargetDate())
                .status(GoalStatus.ACTIVE)
                .progressPercentage(0)
                .build();

        // parentGoalId обрабатываем только если он > 0
        if (request.getParentGoalId() != null && request.getParentGoalId() > 0) {
            Goal parent = goalRepository.findById(request.getParentGoalId())
                    .orElseThrow(() -> new NotFoundException("Parent goal not found"));
            goal.setParentGoal(parent);
        }

        return goalRepository.save(goal);
    }

    @Override
    public Goal getGoalById(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Goal with id " + id + " not found"));
    }

    @Override
    public Page<Goal> getUserGoals(Long userId, Pageable pageable) {
        return goalRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Goal updateGoal(Long goalId, UpdateGoalRequest request) {
        Goal goal = getGoalById(goalId);
        if (request.getTitle() != null) {
            goal.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }
        return goalRepository.save(goal);
    }

    @Override
    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new NotFoundException("Goal with id " + goalId + " not found");
        }
        goalRepository.deleteById(goalId);
    }

    @Override
    public Goal updateProgress(Long goalId, Integer progress) {
        Goal goal = getGoalById(goalId);
        goal.setProgressPercentage(progress);
        if (progress != null && progress == 100) {
            goal.setStatus(GoalStatus.COMPLETED);
            goal.setCompletedAt(LocalDateTime.now());
        }
        return goalRepository.save(goal);
    }

    @Override
    public Goal updateDeadline(Long goalId, LocalDateTime newDeadline) {
        Goal goal = getGoalById(goalId);
        goal.setTargetDate(newDeadline);
        return goalRepository.save(goal);
    }

    @Override
    public GoalStatistics getGoalStatistics(Long userId) {
        List<Goal> goals = goalRepository.findAllByUserId(userId);
        long total = goals.size();
        long completed = goals.stream().filter(g -> g.getStatus() == GoalStatus.COMPLETED).count();
        long overdue = goals.stream()
                .filter(g -> g.getTargetDate() != null
                        && g.getTargetDate().isBefore(LocalDateTime.now())
                        && g.getStatus() != GoalStatus.COMPLETED)
                .count();
        double avgProgress = goals.stream()
                .mapToInt(g -> g.getProgressPercentage() == null ? 0 : g.getProgressPercentage())
                .average()
                .orElse(0.0);

        Map<String, Long> byStatus = new HashMap<>();
        for (GoalStatus status : GoalStatus.values()) {
            byStatus.put(status.name(), goals.stream().filter(g -> g.getStatus() == status).count());
        }

        return GoalStatistics.builder()
                .totalGoals(total)
                .completedGoals(completed)
                .overdueGoals(overdue)
                .averageProgress(avgProgress)
                .goalsByStatus(byStatus)
                .build();
    }

    @Override
    public List<Goal> getGoalsWithApproachingDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inThreeDays = now.plusDays(3);
        return goalRepository.findAllByTargetDateBetween(now, inThreeDays);
    }
}

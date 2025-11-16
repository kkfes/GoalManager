package org.example.goalmanager.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.goalmanager.dto.goal.CreateGoalRequest;
import org.example.goalmanager.dto.goal.GoalResponse;
import org.example.goalmanager.dto.goal.GoalStatistics;
import org.example.goalmanager.dto.goal.UpdateDeadlineRequest;
import org.example.goalmanager.dto.goal.UpdateGoalRequest;
import org.example.goalmanager.dto.goal.UpdateProgressRequest;
import org.example.goalmanager.entity.Goal;
import org.example.goalmanager.service.GoalService;
import org.example.goalmanager.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
@SecurityRequirement(name = "bearerAuth")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<GoalResponse>> getUserGoals(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Long userId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Goal> goalsPage = goalService.getUserGoals(userId, pageable);

        List<GoalResponse> content = goalsPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        Page<GoalResponse> responsePage = new PageImpl<>(content, pageable, goalsPage.getTotalElements());

        return ResponseEntity.ok(responsePage);
    }

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        Long userId = userService.getCurrentUser().getId();
        Goal goal = goalService.createGoal(request, userId);
        return new ResponseEntity<>(toResponse(goal), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable Long id) {
        Goal goal = goalService.getGoalById(id);
        return ResponseEntity.ok(toResponse(goal));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateGoalRequest request) {
        Goal goal = goalService.updateGoal(id, request);
        return ResponseEntity.ok(toResponse(goal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<GoalResponse> updateProgress(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateProgressRequest request) {
        Goal goal = goalService.updateProgress(id, request.getProgress());
        return ResponseEntity.ok(toResponse(goal));
    }

    @PatchMapping("/{id}/deadline")
    public ResponseEntity<GoalResponse> updateDeadline(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateDeadlineRequest request) {
        Goal goal = goalService.updateDeadline(id, request.getTargetDate());
        return ResponseEntity.ok(toResponse(goal));
    }

    @GetMapping("/statistics")
    public ResponseEntity<GoalStatistics> getStatistics() {
        Long userId = userService.getCurrentUser().getId();
        GoalStatistics statistics = goalService.getGoalStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    private GoalResponse toResponse(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .progressPercentage(goal.getProgressPercentage())
                .targetDate(goal.getTargetDate())
                .completedAt(goal.getCompletedAt())
                .status(goal.getStatus())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}

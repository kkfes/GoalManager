package org.example.goalmanager.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.goalmanager.dto.admin.AdminStatistics;
import org.example.goalmanager.dto.user.UserResponse;
import org.example.goalmanager.entity.GoalStatus;
import org.example.goalmanager.entity.User;
import org.example.goalmanager.repository.GoalRepository;
import org.example.goalmanager.repository.UserRepository;
import org.example.goalmanager.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public AdminController(UserService userService, UserRepository userRepository, GoalRepository goalRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userService.getAllUsers(pageable);
        List<UserResponse> content = usersPage.getContent().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        Page<UserResponse> responsePage = new PageImpl<>(content, pageable, usersPage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<Void> resetUserPassword(@PathVariable Long userId) {
        userService.resetUserPassword(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<AdminStatistics> getAdminStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long totalGoals = goalRepository.count();
        long completedGoals = goalRepository.countByStatus(GoalStatus.COMPLETED);

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentRegistrations = userRepository.countByCreatedAtAfter(weekAgo);

        Map<String, Long> systemUsage = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusDays(7);
        long goalsCreatedThisWeek = goalRepository.countByCreatedAtBetween(weekStart, now);
        long goalsCompletedThisWeek = goalRepository.countByCompletedAtBetween(weekStart, now);
        systemUsage.put("goalsCreatedThisWeek", goalsCreatedThisWeek);
        systemUsage.put("goalsCompletedThisWeek", goalsCompletedThisWeek);

        AdminStatistics stats = AdminStatistics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalGoals(totalGoals)
                .completedGoals(completedGoals)
                .recentRegistrations(recentRegistrations)
                .systemUsage(systemUsage)
                .build();

        return ResponseEntity.ok(stats);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

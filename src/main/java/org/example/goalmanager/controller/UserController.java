package org.example.goalmanager.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.goalmanager.dto.user.UpdateUserProfileRequest;
import org.example.goalmanager.dto.user.UserProfileResponse;
import org.example.goalmanager.entity.User;
import org.example.goalmanager.repository.GoalRepository;
import org.example.goalmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final GoalRepository goalRepository;

    public UserController(UserService userService, GoalRepository goalRepository) {
        this.userService = userService;
        this.goalRepository = goalRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        User user = userService.getCurrentUser();
        long goalsCount = goalRepository.findAllByUserId(user.getId()).size();
        long completedGoalsCount = goalRepository.findAllByUserIdAndStatus(user.getId(), org.example.goalmanager.entity.GoalStatus.COMPLETED).size();

        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .goalsCount(goalsCount)
                .completedGoalsCount(completedGoalsCount)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        User user = userService.getCurrentUser();
        user.setUsername(request.getUsername());
        // Сохранение через сервис, чтобы применялись проверки
        userService.updateUser(user);

        long goalsCount = goalRepository.findAllByUserId(user.getId()).size();
        long completedGoalsCount = goalRepository.findAllByUserIdAndStatus(user.getId(), org.example.goalmanager.entity.GoalStatus.COMPLETED).size();

        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .goalsCount(goalsCount)
                .completedGoalsCount(completedGoalsCount)
                .build();
        return ResponseEntity.ok(response);
    }
}

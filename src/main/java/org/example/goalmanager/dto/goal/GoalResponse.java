package org.example.goalmanager.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.goalmanager.entity.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalResponse {

    private Long id;
    private String title;
    private String description;
    private Integer progressPercentage;
    private LocalDateTime targetDate;
    private LocalDateTime completedAt;
    private GoalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GoalResponse> subGoals;
}

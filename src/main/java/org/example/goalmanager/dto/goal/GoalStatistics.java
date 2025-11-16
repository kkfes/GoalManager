package org.example.goalmanager.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalStatistics {

    private Long totalGoals;
    private Long completedGoals;
    private Long overdueGoals;
    private Double averageProgress;
    private Map<String, Long> goalsByStatus;
}


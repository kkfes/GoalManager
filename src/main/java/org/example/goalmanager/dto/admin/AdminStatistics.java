package org.example.goalmanager.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStatistics {

    private Long totalUsers;
    private Long activeUsers;
    private Long totalGoals;
    private Long completedGoals;
    private Long recentRegistrations;
    private Map<String, Long> systemUsage;
}


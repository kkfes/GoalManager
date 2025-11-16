package org.example.goalmanager.dto.goal;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateGoalRequest {

    private String title;

    private String description;

    private LocalDateTime targetDate;
}


package org.example.goalmanager.dto.goal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateGoalRequest {

    @NotBlank
    private String title;

    private String description;

    private Long parentGoalId;

    private LocalDateTime targetDate;
}


package org.example.goalmanager.dto.goal;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateDeadlineRequest {

    private LocalDateTime targetDate;
}


package org.example.goalmanager.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    @NotBlank
    private String username;
}


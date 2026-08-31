package com.fit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartWorkoutRequest {
    private Long planId;

    @NotBlank(message = "Workout name is required")
    private String name;
}
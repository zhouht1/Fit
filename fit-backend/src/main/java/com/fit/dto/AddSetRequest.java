package com.fit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddSetRequest {
    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @Min(value = 0, message = "Weight must be >= 0")
    private Double weight;

    @Min(value = 0, message = "Reps must be >= 0")
    private Integer reps;

    private Boolean completed;
}
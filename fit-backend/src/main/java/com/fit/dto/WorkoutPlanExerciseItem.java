package com.fit.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkoutPlanExerciseItem {
    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @Min(value = 1, message = "Sets must be at least 1")
    private Integer targetSets;

    private String targetReps;

    private Integer orderNum;
}
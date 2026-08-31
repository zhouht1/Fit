package com.fit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class WorkoutPlanRequest {
    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name must be at most 100 characters")
    private String name;

    private String goal;

    private Integer trainingDays;

    private Integer estimatedDuration;

    private List<WorkoutPlanExerciseItem> exercises;
}
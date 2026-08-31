package com.fit.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkoutSetVO {
    private Long id;
    private Long workoutId;
    private Long exerciseId;
    private String exerciseName;
    private Integer setNumber;
    private Double weight;
    private Integer reps;
    private Double volume;
    private Boolean completed;
}
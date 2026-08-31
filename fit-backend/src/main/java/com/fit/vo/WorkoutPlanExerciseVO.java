package com.fit.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkoutPlanExerciseVO {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private String muscleGroup;
    private String equipment;
    private Integer targetSets;
    private String targetReps;
    private Integer orderNum;
}
package com.fit.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WorkoutPlanVO {
    private Long id;
    private Long userId;
    private String name;
    private String goal;
    private Integer trainingDays;
    private Integer estimatedDuration;
    private List<WorkoutPlanExerciseVO> exercises;
}
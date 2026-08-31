package com.fit.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgressiveOverloadVO {
    private String exerciseName;
    private Double previousWeight;
    private Integer previousReps;
    private Double currentWeight;
    private Integer currentReps;
    private String suggestion;
}
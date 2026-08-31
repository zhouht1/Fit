package com.fit.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalRecordVO {
    private Long exerciseId;
    private String exerciseName;
    private Double maxWeight;
    private Integer maxReps;
    private Double maxVolume;
    private String achievedAt;
}
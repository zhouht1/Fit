package com.fit.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseVO {
    private Long id;
    private String name;
    private String muscleGroup;
    private String equipment;
    private String description;
}
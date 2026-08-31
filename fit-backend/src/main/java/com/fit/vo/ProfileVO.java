package com.fit.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileVO {
    private Long id;
    private Long userId;
    private String name;
    private Integer age;
    private Double height;
    private Double weight;
    private String gender;
    private String fitnessGoal;
    private Integer trainingFrequency;
    private String experience;
}
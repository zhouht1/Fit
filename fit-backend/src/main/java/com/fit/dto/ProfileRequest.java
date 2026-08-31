package com.fit.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;

    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must be at most 300 cm")
    private Double height;

    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight must be at most 500 kg")
    private Double weight;

    private String gender;

    private String fitnessGoal;

    @Min(value = 1, message = "Training frequency must be at least 1")
    @Max(value = 7, message = "Training frequency must be at most 7")
    private Integer trainingFrequency;

    private String experience;
}
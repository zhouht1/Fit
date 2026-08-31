package com.fit.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BodyMeasurementRequest {
    @Min(value = 20, message = "Weight must be at least 20 kg")
    private Double weight;

    private Double bodyFat;
    private Double chest;
    private Double waist;
    private Double hip;
    private Double arm;
    private Double thigh;
    private String note;
}
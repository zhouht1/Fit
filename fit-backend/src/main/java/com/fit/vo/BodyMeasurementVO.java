package com.fit.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class BodyMeasurementVO {
    private Long id;
    private Double weight;
    private Double bodyFat;
    private Double chest;
    private Double waist;
    private Double hip;
    private Double arm;
    private Double thigh;
    private String note;
    private LocalDate measuredAt;
}
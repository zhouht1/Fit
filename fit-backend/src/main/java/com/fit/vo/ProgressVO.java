package com.fit.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ProgressVO {
    private LocalDate date;
    private Double weight;
    private Double trainingVolume;
    private Integer trainingDuration;
}
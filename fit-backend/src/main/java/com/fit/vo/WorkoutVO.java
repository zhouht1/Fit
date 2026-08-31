package com.fit.vo;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class WorkoutVO {
    private Long id;
    private Long userId;
    private Long planId;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private Double totalVolume;
    private Integer totalSets;
    private Integer exerciseCount;
    private String status;
    private List<WorkoutSetVO> sets;
}
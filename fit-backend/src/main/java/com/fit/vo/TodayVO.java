package com.fit.vo;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TodayVO {
    private String greeting;
    private String userName;
    private String date;
    private WorkoutInfo workout;
    private List<Boolean> weeklyActivity;
    private WeightInfo weight;
    private RecoveryInfo recovery;
    private StreakVO streak;

    @Data
    @Builder
    public static class WorkoutInfo {
        private Long planId;
        private String name;
        private String muscleGroups;
        private Integer estimatedDuration;
        private boolean hasWorkout;
    }

    @Data
    @Builder
    public static class WeightInfo {
        private Double current;
        private Double change;
    }

    @Data
    @Builder
    public static class RecoveryInfo {
        private String status;
        private String suggestion;
    }
}
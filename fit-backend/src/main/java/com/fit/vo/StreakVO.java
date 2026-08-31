package com.fit.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreakVO {
    private int currentStreak;
    private int longestStreak;
    private String lastWorkoutDate;
}
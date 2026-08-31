package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("workout_plan_exercise")
public class WorkoutPlanExercise {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long exerciseId;
    private Integer targetSets;
    private String targetReps;
    private Integer orderNum;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
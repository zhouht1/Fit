package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("workout_plan")
public class WorkoutPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String goal;
    private Integer trainingDays;
    private Integer estimatedDuration;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
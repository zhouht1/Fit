package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("workout_set")
public class WorkoutSet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long workoutId;
    private Long exerciseId;
    private Integer setNumber;
    private Double weight;
    private Integer reps;
    private Boolean completed;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
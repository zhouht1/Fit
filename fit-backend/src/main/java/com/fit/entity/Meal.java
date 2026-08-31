package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("meal")
public class Meal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String mealType;
    private Integer calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private LocalDateTime mealTime;
    private String notes;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
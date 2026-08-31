package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("personal_record")
public class PersonalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long exerciseId;
    private String recordType;
    private Double value;
    private Long workoutId;
    private LocalDateTime achievedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
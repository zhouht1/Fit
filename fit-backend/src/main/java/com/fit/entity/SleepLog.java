package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sleep_log")
public class SleepLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private LocalDateTime sleepTime;
    private LocalDateTime wakeTime;
    private Integer durationMinutes;
    private String quality;
    private String notes;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
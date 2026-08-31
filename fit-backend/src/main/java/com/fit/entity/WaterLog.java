package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("water_log")
public class WaterLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer amountMl;
    private LocalDateTime loggedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
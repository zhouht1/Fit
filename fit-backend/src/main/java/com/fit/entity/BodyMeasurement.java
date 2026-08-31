package com.fit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("body_measurement")
public class BodyMeasurement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Double weight;
    private Double bodyFat;
    private Double chest;
    private Double waist;
    private Double hip;
    private Double arm;
    private Double thigh;
    private String note;
    private LocalDate measuredAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
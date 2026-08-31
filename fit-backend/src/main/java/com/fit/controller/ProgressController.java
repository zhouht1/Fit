package com.fit.controller;

import com.fit.common.Result;
import com.fit.dto.BodyMeasurementRequest;
import com.fit.entity.User;
import com.fit.service.BodyMeasurementService;
import com.fit.vo.BodyMeasurementVO;
import com.fit.vo.ProgressVO;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProgressController {

    private final BodyMeasurementService bodyMeasurementService;

    public ProgressController(BodyMeasurementService bodyMeasurementService) {
        this.bodyMeasurementService = bodyMeasurementService;
    }

    @GetMapping("/body-measurements")
    public Result<List<BodyMeasurementVO>> getMeasurements(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "30d") String period) {
        return Result.success(bodyMeasurementService.getMeasurements(user.getId(), period));
    }

    @PostMapping("/body-measurements")
    public Result<BodyMeasurementVO> addMeasurement(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BodyMeasurementRequest request) {
        return Result.success(bodyMeasurementService.addMeasurement(user.getId(), request));
    }

    @GetMapping("/progress")
    public Result<List<ProgressVO>> getProgress(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "30d") String period) {
        return Result.success(bodyMeasurementService.getProgress(user.getId(), period));
    }
}
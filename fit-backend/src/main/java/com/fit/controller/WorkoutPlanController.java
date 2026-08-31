package com.fit.controller;

import com.fit.common.Result;
import com.fit.dto.WorkoutPlanRequest;
import com.fit.entity.User;
import com.fit.service.WorkoutPlanService;
import com.fit.vo.WorkoutPlanVO;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-plans")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @GetMapping
    public Result<List<WorkoutPlanVO>> getPlans(@AuthenticationPrincipal User user) {
        return Result.success(workoutPlanService.getPlans(user.getId()));
    }

    @PostMapping
    public Result<WorkoutPlanVO> createPlan(@AuthenticationPrincipal User user,
                                            @Valid @RequestBody WorkoutPlanRequest request) {
        return Result.success(workoutPlanService.createPlan(user.getId(), request));
    }

    @GetMapping("/{id}")
    public Result<WorkoutPlanVO> getPlan(@AuthenticationPrincipal User user,
                                         @PathVariable Long id) {
        return Result.success(workoutPlanService.getPlanById(user.getId(), id));
    }

    @PutMapping("/{id}")
    public Result<WorkoutPlanVO> updatePlan(@AuthenticationPrincipal User user,
                                            @PathVariable Long id,
                                            @Valid @RequestBody WorkoutPlanRequest request) {
        return Result.success(workoutPlanService.updatePlan(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public Result<?> deletePlan(@AuthenticationPrincipal User user,
                                @PathVariable Long id) {
        workoutPlanService.deletePlan(user.getId(), id);
        return Result.success();
    }
}
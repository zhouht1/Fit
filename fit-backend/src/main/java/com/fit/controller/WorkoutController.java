package com.fit.controller;

import com.fit.common.Result;
import com.fit.dto.AddSetRequest;
import com.fit.dto.StartWorkoutRequest;
import com.fit.entity.User;
import com.fit.service.WorkoutService;
import com.fit.vo.WorkoutSetVO;
import com.fit.vo.WorkoutVO;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public Result<WorkoutVO> startWorkout(@AuthenticationPrincipal User user,
                                          @Valid @RequestBody StartWorkoutRequest request) {
        return Result.success(workoutService.startWorkout(user.getId(), request));
    }

    @GetMapping
    public Result<List<WorkoutVO>> getWorkouts(@AuthenticationPrincipal User user) {
        return Result.success(workoutService.getWorkouts(user.getId()));
    }

    @GetMapping("/{id}")
    public Result<WorkoutVO> getWorkout(@AuthenticationPrincipal User user,
                                        @PathVariable Long id) {
        return Result.success(workoutService.getWorkoutById(user.getId(), id));
    }

    @PostMapping("/{id}/sets")
    public Result<WorkoutSetVO> addSet(@AuthenticationPrincipal User user,
                                       @PathVariable Long id,
                                       @Valid @RequestBody AddSetRequest request) {
        return Result.success(workoutService.addSet(user.getId(), id, request));
    }

    @PostMapping("/{id}/finish")
    public Result<WorkoutVO> finishWorkout(@AuthenticationPrincipal User user,
                                           @PathVariable Long id) {
        return Result.success(workoutService.finishWorkout(user.getId(), id));
    }
}
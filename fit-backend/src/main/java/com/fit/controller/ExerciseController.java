package com.fit.controller;

import com.fit.common.Result;
import com.fit.service.ExerciseService;
import com.fit.vo.ExerciseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public Result<List<ExerciseVO>> getExercises(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String muscleGroup) {
        List<ExerciseVO> exercises = exerciseService.getAllExercises(keyword, muscleGroup);
        return Result.success(exercises);
    }

    @GetMapping("/{id}")
    public Result<ExerciseVO> getExercise(@PathVariable Long id) {
        ExerciseVO exercise = exerciseService.getExerciseById(id);
        return Result.success(exercise);
    }
}
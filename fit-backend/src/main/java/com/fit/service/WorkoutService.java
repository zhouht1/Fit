package com.fit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fit.dto.AddSetRequest;
import com.fit.dto.StartWorkoutRequest;
import com.fit.entity.Workout;
import com.fit.vo.WorkoutSetVO;
import com.fit.vo.WorkoutVO;

import java.util.List;

public interface WorkoutService extends IService<Workout> {
    WorkoutVO startWorkout(Long userId, StartWorkoutRequest request);
    List<WorkoutVO> getWorkouts(Long userId);
    WorkoutVO getWorkoutById(Long userId, Long workoutId);
    WorkoutSetVO addSet(Long userId, Long workoutId, AddSetRequest request);
    WorkoutVO finishWorkout(Long userId, Long workoutId);
}
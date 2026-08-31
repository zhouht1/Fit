package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.WorkoutPlanExercise;
import com.fit.mapper.WorkoutPlanExerciseMapper;
import com.fit.service.WorkoutPlanExerciseService;
import org.springframework.stereotype.Service;

@Service
public class WorkoutPlanExerciseServiceImpl extends ServiceImpl<WorkoutPlanExerciseMapper, WorkoutPlanExercise> implements WorkoutPlanExerciseService {
}
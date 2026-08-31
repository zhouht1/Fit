package com.fit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.dto.AddSetRequest;
import com.fit.dto.StartWorkoutRequest;
import com.fit.entity.Exercise;
import com.fit.entity.Workout;
import com.fit.entity.WorkoutSet;
import com.fit.exception.BusinessException;
import com.fit.mapper.WorkoutMapper;
import com.fit.service.ExerciseService;
import com.fit.service.WorkoutPlanExerciseService;
import com.fit.service.WorkoutPlanService;
import com.fit.service.WorkoutService;
import com.fit.service.WorkoutSetService;
import com.fit.vo.WorkoutSetVO;
import com.fit.vo.WorkoutVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkoutServiceImpl extends ServiceImpl<WorkoutMapper, Workout> implements WorkoutService {

    private final WorkoutSetService workoutSetService;
    private final ExerciseService exerciseService;
    private final WorkoutPlanService workoutPlanService;
    private final WorkoutPlanExerciseService planExerciseService;

    public WorkoutServiceImpl(WorkoutSetService workoutSetService, ExerciseService exerciseService,
                              WorkoutPlanService workoutPlanService, WorkoutPlanExerciseService planExerciseService) {
        this.workoutSetService = workoutSetService;
        this.exerciseService = exerciseService;
        this.workoutPlanService = workoutPlanService;
        this.planExerciseService = planExerciseService;
    }

    @Override
    @Transactional
    public WorkoutVO startWorkout(Long userId, StartWorkoutRequest request) {
        Workout workout = new Workout();
        workout.setUserId(userId);
        workout.setPlanId(request.getPlanId());
        workout.setName(request.getName());
        workout.setStartTime(LocalDateTime.now());
        workout.setStatus("in_progress");
        save(workout);

        return toVO(workout);
    }

    @Override
    public List<WorkoutVO> getWorkouts(Long userId) {
        List<Workout> workouts = list(new LambdaQueryWrapper<Workout>()
                .eq(Workout::getUserId, userId)
                .orderByDesc(Workout::getCreatedAt));

        return workouts.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public WorkoutVO getWorkoutById(Long userId, Long workoutId) {
        Workout workout = getById(workoutId);
        if (workout == null || !workout.getUserId().equals(userId)) {
            throw new BusinessException(404, "Workout not found");
        }
        return toVO(workout);
    }

    @Override
    @Transactional
    public WorkoutSetVO addSet(Long userId, Long workoutId, AddSetRequest request) {
        Workout workout = getById(workoutId);
        if (workout == null || !workout.getUserId().equals(userId)) {
            throw new BusinessException(404, "Workout not found");
        }
        if (!"in_progress".equals(workout.getStatus())) {
            throw new BusinessException(400, "Workout is already finished");
        }

        // Get next set number for this exercise
        long setCount = workoutSetService.count(new LambdaQueryWrapper<WorkoutSet>()
                .eq(WorkoutSet::getWorkoutId, workoutId)
                .eq(WorkoutSet::getExerciseId, request.getExerciseId()));

        WorkoutSet set = new WorkoutSet();
        set.setWorkoutId(workoutId);
        set.setExerciseId(request.getExerciseId());
        set.setSetNumber((int) setCount + 1);
        set.setWeight(request.getWeight() != null ? request.getWeight() : 0);
        set.setReps(request.getReps() != null ? request.getReps() : 0);
        set.setCompleted(request.getCompleted() != null ? request.getCompleted() : true);
        workoutSetService.save(set);

        // Update workout stats
        updateWorkoutStats(workout);

        Exercise exercise = exerciseService.getById(request.getExerciseId());
        double volume = set.getWeight() * set.getReps();

        return WorkoutSetVO.builder()
                .id(set.getId())
                .workoutId(set.getWorkoutId())
                .exerciseId(set.getExerciseId())
                .exerciseName(exercise != null ? exercise.getName() : "Unknown")
                .setNumber(set.getSetNumber())
                .weight(set.getWeight())
                .reps(set.getReps())
                .volume(volume)
                .completed(set.getCompleted())
                .build();
    }

    @Override
    @Transactional
    public WorkoutVO finishWorkout(Long userId, Long workoutId) {
        Workout workout = getById(workoutId);
        if (workout == null || !workout.getUserId().equals(userId)) {
            throw new BusinessException(404, "Workout not found");
        }
        if (!"in_progress".equals(workout.getStatus())) {
            throw new BusinessException(400, "Workout is already finished");
        }

        workout.setEndTime(LocalDateTime.now());
        workout.setStatus("completed");

        if (workout.getStartTime() != null) {
            workout.setDuration((int) ChronoUnit.MINUTES.between(workout.getStartTime(), workout.getEndTime()));
        }

        updateWorkoutStats(workout);
        updateById(workout);

        return toVO(workout);
    }

    private void updateWorkoutStats(Workout workout) {
        List<WorkoutSet> sets = workoutSetService.list(new LambdaQueryWrapper<WorkoutSet>()
                .eq(WorkoutSet::getWorkoutId, workout.getId()));

        double totalVolume = sets.stream()
                .mapToDouble(s -> s.getWeight() * s.getReps())
                .sum();
        long totalSets = sets.size();
        long exerciseCount = sets.stream()
                .map(WorkoutSet::getExerciseId)
                .distinct()
                .count();

        workout.setTotalVolume(totalVolume);
        workout.setTotalSets((int) totalSets);
        workout.setExerciseCount((int) exerciseCount);
        updateById(workout);
    }

    private WorkoutVO toVO(Workout workout) {
        List<WorkoutSet> sets = workoutSetService.list(new LambdaQueryWrapper<WorkoutSet>()
                .eq(WorkoutSet::getWorkoutId, workout.getId())
                .orderByAsc(WorkoutSet::getExerciseId, WorkoutSet::getSetNumber));

        List<Long> exerciseIds = sets.stream()
                .map(WorkoutSet::getExerciseId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Exercise> exerciseMap = exerciseIds.isEmpty()
                ? Map.of()
                : exerciseService.listByIds(exerciseIds).stream()
                    .collect(Collectors.toMap(Exercise::getId, e -> e));

        List<WorkoutSetVO> setVOs = new ArrayList<>();
        for (WorkoutSet set : sets) {
            Exercise exercise = exerciseMap.get(set.getExerciseId());
            setVOs.add(WorkoutSetVO.builder()
                    .id(set.getId())
                    .workoutId(set.getWorkoutId())
                    .exerciseId(set.getExerciseId())
                    .exerciseName(exercise != null ? exercise.getName() : "Unknown")
                    .setNumber(set.getSetNumber())
                    .weight(set.getWeight())
                    .reps(set.getReps())
                    .volume(set.getWeight() * set.getReps())
                    .completed(set.getCompleted())
                    .build());
        }

        return WorkoutVO.builder()
                .id(workout.getId())
                .userId(workout.getUserId())
                .planId(workout.getPlanId())
                .name(workout.getName())
                .startTime(workout.getStartTime())
                .endTime(workout.getEndTime())
                .duration(workout.getDuration())
                .totalVolume(workout.getTotalVolume())
                .totalSets(workout.getTotalSets())
                .exerciseCount(workout.getExerciseCount())
                .status(workout.getStatus())
                .sets(setVOs)
                .build();
    }
}
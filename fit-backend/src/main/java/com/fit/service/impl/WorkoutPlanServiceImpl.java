package com.fit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.dto.WorkoutPlanExerciseItem;
import com.fit.dto.WorkoutPlanRequest;
import com.fit.entity.Exercise;
import com.fit.entity.WorkoutPlan;
import com.fit.entity.WorkoutPlanExercise;
import com.fit.exception.BusinessException;
import com.fit.mapper.WorkoutPlanMapper;
import com.fit.service.ExerciseService;
import com.fit.service.WorkoutPlanExerciseService;
import com.fit.service.WorkoutPlanService;
import com.fit.vo.WorkoutPlanExerciseVO;
import com.fit.vo.WorkoutPlanVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanServiceImpl extends ServiceImpl<WorkoutPlanMapper, WorkoutPlan> implements WorkoutPlanService {

    private final WorkoutPlanExerciseService planExerciseService;
    private final ExerciseService exerciseService;

    public WorkoutPlanServiceImpl(WorkoutPlanExerciseService planExerciseService, ExerciseService exerciseService) {
        this.planExerciseService = planExerciseService;
        this.exerciseService = exerciseService;
    }

    @Override
    public List<WorkoutPlanVO> getPlans(Long userId) {
        List<WorkoutPlan> plans = list(new LambdaQueryWrapper<WorkoutPlan>()
                .eq(WorkoutPlan::getUserId, userId)
                .orderByDesc(WorkoutPlan::getCreatedAt));

        return plans.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public WorkoutPlanVO getPlanById(Long userId, Long planId) {
        WorkoutPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(404, "Workout plan not found");
        }
        return toVO(plan);
    }

    @Override
    @Transactional
    public WorkoutPlanVO createPlan(Long userId, WorkoutPlanRequest request) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setUserId(userId);
        plan.setName(request.getName());
        plan.setGoal(request.getGoal());
        plan.setTrainingDays(request.getTrainingDays());
        plan.setEstimatedDuration(request.getEstimatedDuration());
        save(plan);

        saveExercises(plan.getId(), request.getExercises());
        return toVO(plan);
    }

    @Override
    @Transactional
    public WorkoutPlanVO updatePlan(Long userId, Long planId, WorkoutPlanRequest request) {
        WorkoutPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(404, "Workout plan not found");
        }

        plan.setName(request.getName());
        plan.setGoal(request.getGoal());
        plan.setTrainingDays(request.getTrainingDays());
        plan.setEstimatedDuration(request.getEstimatedDuration());
        updateById(plan);

        // Replace exercises
        planExerciseService.remove(new LambdaQueryWrapper<WorkoutPlanExercise>()
                .eq(WorkoutPlanExercise::getPlanId, planId));
        saveExercises(planId, request.getExercises());

        return toVO(plan);
    }

    @Override
    @Transactional
    public void deletePlan(Long userId, Long planId) {
        WorkoutPlan plan = getById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new BusinessException(404, "Workout plan not found");
        }
        removeById(planId);
    }

    private void saveExercises(Long planId, List<WorkoutPlanExerciseItem> items) {
        if (items == null || items.isEmpty()) return;

        for (WorkoutPlanExerciseItem item : items) {
            WorkoutPlanExercise wpe = new WorkoutPlanExercise();
            wpe.setPlanId(planId);
            wpe.setExerciseId(item.getExerciseId());
            wpe.setTargetSets(item.getTargetSets() != null ? item.getTargetSets() : 3);
            wpe.setTargetReps(item.getTargetReps());
            wpe.setOrderNum(item.getOrderNum() != null ? item.getOrderNum() : 0);
            planExerciseService.save(wpe);
        }
    }

    private WorkoutPlanVO toVO(WorkoutPlan plan) {
        List<WorkoutPlanExercise> planExercises = planExerciseService.list(
                new LambdaQueryWrapper<WorkoutPlanExercise>()
                        .eq(WorkoutPlanExercise::getPlanId, plan.getId())
                        .orderByAsc(WorkoutPlanExercise::getOrderNum));

        // Load all exercises in one batch
        List<Long> exerciseIds = planExercises.stream()
                .map(WorkoutPlanExercise::getExerciseId)
                .collect(Collectors.toList());
        Map<Long, Exercise> exerciseMap = exerciseIds.isEmpty()
                ? Map.of()
                : exerciseService.listByIds(exerciseIds).stream()
                    .collect(Collectors.toMap(Exercise::getId, e -> e));

        List<WorkoutPlanExerciseVO> exerciseVOs = new ArrayList<>();
        for (WorkoutPlanExercise wpe : planExercises) {
            Exercise exercise = exerciseMap.get(wpe.getExerciseId());
            exerciseVOs.add(WorkoutPlanExerciseVO.builder()
                    .id(wpe.getId())
                    .exerciseId(wpe.getExerciseId())
                    .exerciseName(exercise != null ? exercise.getName() : "Unknown")
                    .muscleGroup(exercise != null ? exercise.getMuscleGroup() : null)
                    .equipment(exercise != null ? exercise.getEquipment() : null)
                    .targetSets(wpe.getTargetSets())
                    .targetReps(wpe.getTargetReps())
                    .orderNum(wpe.getOrderNum())
                    .build());
        }

        return WorkoutPlanVO.builder()
                .id(plan.getId())
                .userId(plan.getUserId())
                .name(plan.getName())
                .goal(plan.getGoal())
                .trainingDays(plan.getTrainingDays())
                .estimatedDuration(plan.getEstimatedDuration())
                .exercises(exerciseVOs)
                .build();
    }
}
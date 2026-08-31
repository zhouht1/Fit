package com.fit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fit.dto.WorkoutPlanRequest;
import com.fit.entity.WorkoutPlan;
import com.fit.vo.WorkoutPlanVO;

import java.util.List;

public interface WorkoutPlanService extends IService<WorkoutPlan> {
    List<WorkoutPlanVO> getPlans(Long userId);
    WorkoutPlanVO getPlanById(Long userId, Long planId);
    WorkoutPlanVO createPlan(Long userId, WorkoutPlanRequest request);
    WorkoutPlanVO updatePlan(Long userId, Long planId, WorkoutPlanRequest request);
    void deletePlan(Long userId, Long planId);
}
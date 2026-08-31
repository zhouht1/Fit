package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.WorkoutPlan;
import com.fit.mapper.WorkoutPlanMapper;
import com.fit.service.WorkoutPlanService;
import org.springframework.stereotype.Service;

@Service
public class WorkoutPlanServiceImpl extends ServiceImpl<WorkoutPlanMapper, WorkoutPlan> implements WorkoutPlanService {
}
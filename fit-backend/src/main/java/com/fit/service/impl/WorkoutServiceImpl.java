package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.Workout;
import com.fit.mapper.WorkoutMapper;
import com.fit.service.WorkoutService;
import org.springframework.stereotype.Service;

@Service
public class WorkoutServiceImpl extends ServiceImpl<WorkoutMapper, Workout> implements WorkoutService {
}
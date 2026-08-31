package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.WorkoutSet;
import com.fit.mapper.WorkoutSetMapper;
import com.fit.service.WorkoutSetService;
import org.springframework.stereotype.Service;

@Service
public class WorkoutSetServiceImpl extends ServiceImpl<WorkoutSetMapper, WorkoutSet> implements WorkoutSetService {
}
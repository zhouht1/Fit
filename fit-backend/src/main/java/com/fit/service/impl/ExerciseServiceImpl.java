package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.Exercise;
import com.fit.mapper.ExerciseMapper;
import com.fit.service.ExerciseService;
import org.springframework.stereotype.Service;

@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements ExerciseService {
}
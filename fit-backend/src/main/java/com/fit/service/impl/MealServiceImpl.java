package com.fit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.Meal;
import com.fit.mapper.MealMapper;
import com.fit.service.MealService;
import org.springframework.stereotype.Service;

@Service
public class MealServiceImpl extends ServiceImpl<MealMapper, Meal> implements MealService {
}
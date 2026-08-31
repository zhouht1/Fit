package com.fit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fit.entity.Meal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MealMapper extends BaseMapper<Meal> {
}
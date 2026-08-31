package com.fit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fit.entity.Workout;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkoutMapper extends BaseMapper<Workout> {
}
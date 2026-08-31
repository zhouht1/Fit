package com.fit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.entity.Exercise;
import com.fit.exception.BusinessException;
import com.fit.mapper.ExerciseMapper;
import com.fit.service.ExerciseService;
import com.fit.vo.ExerciseVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExerciseServiceImpl extends ServiceImpl<ExerciseMapper, Exercise> implements ExerciseService {

    @Override
    public List<ExerciseVO> getAllExercises(String keyword, String muscleGroup) {
        LambdaQueryWrapper<Exercise> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Exercise::getName, keyword);
        }
        if (StringUtils.hasText(muscleGroup)) {
            wrapper.eq(Exercise::getMuscleGroup, muscleGroup);
        }
        wrapper.orderByAsc(Exercise::getName);

        return list(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public ExerciseVO getExerciseById(Long id) {
        Exercise exercise = getById(id);
        if (exercise == null) {
            throw new BusinessException(404, "Exercise not found");
        }
        return toVO(exercise);
    }

    private ExerciseVO toVO(Exercise exercise) {
        return ExerciseVO.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .muscleGroup(exercise.getMuscleGroup())
                .equipment(exercise.getEquipment())
                .description(exercise.getDescription())
                .build();
    }
}
package com.fit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fit.entity.Exercise;
import com.fit.vo.ExerciseVO;

import java.util.List;

public interface ExerciseService extends IService<Exercise> {
    List<ExerciseVO> getAllExercises(String keyword, String muscleGroup);
    ExerciseVO getExerciseById(Long id);
}
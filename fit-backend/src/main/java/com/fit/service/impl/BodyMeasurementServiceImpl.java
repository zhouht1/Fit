package com.fit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit.dto.BodyMeasurementRequest;
import com.fit.entity.BodyMeasurement;
import com.fit.entity.Workout;
import com.fit.mapper.BodyMeasurementMapper;
import com.fit.service.BodyMeasurementService;
import com.fit.service.WorkoutService;
import com.fit.vo.BodyMeasurementVO;
import com.fit.vo.ProgressVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BodyMeasurementServiceImpl extends ServiceImpl<BodyMeasurementMapper, BodyMeasurement> implements BodyMeasurementService {

    private final WorkoutService workoutService;

    public BodyMeasurementServiceImpl(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @Override
    public List<BodyMeasurementVO> getMeasurements(Long userId, String period) {
        LocalDate since = calculateSince(period);
        LambdaQueryWrapper<BodyMeasurement> wrapper = new LambdaQueryWrapper<BodyMeasurement>()
                .eq(BodyMeasurement::getUserId, userId)
                .orderByAsc(BodyMeasurement::getMeasuredAt);

        if (since != null) {
            wrapper.ge(BodyMeasurement::getMeasuredAt, since);
        }

        return list(wrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BodyMeasurementVO addMeasurement(Long userId, BodyMeasurementRequest request) {
        BodyMeasurement bm = new BodyMeasurement();
        bm.setUserId(userId);
        bm.setWeight(request.getWeight());
        bm.setBodyFat(request.getBodyFat());
        bm.setChest(request.getChest());
        bm.setWaist(request.getWaist());
        bm.setHip(request.getHip());
        bm.setArm(request.getArm());
        bm.setThigh(request.getThigh());
        bm.setNote(request.getNote());
        bm.setMeasuredAt(LocalDate.now());
        save(bm);
        return toVO(bm);
    }

    @Override
    public List<ProgressVO> getProgress(Long userId, String period) {
        LocalDate since = calculateSince(period);

        // Get body measurements for weight trend
        LambdaQueryWrapper<BodyMeasurement> bmWrapper = new LambdaQueryWrapper<BodyMeasurement>()
                .eq(BodyMeasurement::getUserId, userId)
                .orderByAsc(BodyMeasurement::getMeasuredAt);
        if (since != null) {
            bmWrapper.ge(BodyMeasurement::getMeasuredAt, since);
        }
        Map<LocalDate, Double> weightByDate = list(bmWrapper).stream()
                .collect(Collectors.toMap(BodyMeasurement::getMeasuredAt, BodyMeasurement::getWeight, (a, b) -> b));

        // Get workouts for volume/duration
        LambdaQueryWrapper<Workout> wWrapper = new LambdaQueryWrapper<Workout>()
                .eq(Workout::getUserId, userId)
                .eq(Workout::getStatus, "completed")
                .orderByAsc(Workout::getStartTime);
        if (since != null) {
            wWrapper.ge(Workout::getStartTime, since.atStartOfDay());
        }

        List<Workout> workouts = workoutService.list(wWrapper);
        Map<LocalDate, List<Workout>> workoutsByDate = workouts.stream()
                .collect(Collectors.groupingBy(w -> w.getStartTime().toLocalDate()));

        // Merge all dates
        Set<LocalDate> allDates = new TreeSet<>();
        allDates.addAll(weightByDate.keySet());
        allDates.addAll(workoutsByDate.keySet());

        List<ProgressVO> progress = new ArrayList<>();
        for (LocalDate date : allDates) {
            double volume = workoutsByDate.getOrDefault(date, List.of()).stream()
                    .mapToDouble(w -> w.getTotalVolume() != null ? w.getTotalVolume() : 0)
                    .sum();
            int duration = workoutsByDate.getOrDefault(date, List.of()).stream()
                    .mapToInt(w -> w.getDuration() != null ? w.getDuration() : 0)
                    .sum();

            progress.add(ProgressVO.builder()
                    .date(date)
                    .weight(weightByDate.get(date))
                    .trainingVolume(volume)
                    .trainingDuration(duration)
                    .build());
        }

        return progress;
    }

    private BodyMeasurementVO toVO(BodyMeasurement bm) {
        return BodyMeasurementVO.builder()
                .id(bm.getId())
                .weight(bm.getWeight())
                .bodyFat(bm.getBodyFat())
                .chest(bm.getChest())
                .waist(bm.getWaist())
                .hip(bm.getHip())
                .arm(bm.getArm())
                .thigh(bm.getThigh())
                .note(bm.getNote())
                .measuredAt(bm.getMeasuredAt())
                .build();
    }

    private LocalDate calculateSince(String period) {
        if (period == null || "all".equalsIgnoreCase(period)) return null;
        return switch (period.toLowerCase()) {
            case "7d" -> LocalDate.now().minusDays(7);
            case "30d" -> LocalDate.now().minusDays(30);
            case "90d" -> LocalDate.now().minusDays(90);
            default -> null;
        };
    }
}
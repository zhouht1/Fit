package com.fit.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fit.entity.*;
import com.fit.vo.StreakVO;
import com.fit.vo.TodayVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TodayService {

    private final UserService userService;
    private final ProfileService profileService;
    private final WorkoutPlanService workoutPlanService;
    private final WorkoutPlanExerciseService planExerciseService;
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;
    private final BodyMeasurementService bodyMeasurementService;
    private final StatsService statsService;

    public TodayService(UserService userService, ProfileService profileService,
                        WorkoutPlanService workoutPlanService, WorkoutPlanExerciseService planExerciseService,
                        ExerciseService exerciseService, WorkoutService workoutService,
                        BodyMeasurementService bodyMeasurementService, StatsService statsService) {
        this.userService = userService;
        this.profileService = profileService;
        this.workoutPlanService = workoutPlanService;
        this.planExerciseService = planExerciseService;
        this.exerciseService = exerciseService;
        this.workoutService = workoutService;
        this.bodyMeasurementService = bodyMeasurementService;
        this.statsService = statsService;
    }

    public TodayVO getToday(Long userId) {
        LocalDate today = LocalDate.now();
        User user = userService.getById(userId);
        Profile profile = profileService.getOne(new LambdaQueryWrapper<Profile>()
                .eq(Profile::getUserId, userId));

        // Greeting
        String greeting = buildGreeting();
        String userName = profile != null && profile.getName() != null ? profile.getName() : user.getUsername();

        // Today's workout - check if there's a plan for today
        List<WorkoutPlan> plans = workoutPlanService.list(new LambdaQueryWrapper<WorkoutPlan>()
                .eq(WorkoutPlan::getUserId, userId)
                .orderByDesc(WorkoutPlan::getCreatedAt));
        TodayVO.WorkoutInfo workoutInfo = buildWorkoutInfo(plans);

        // Weekly activity (last 7 days)
        List<Boolean> weeklyActivity = buildWeeklyActivity(userId, today);

        // Weight
        TodayVO.WeightInfo weightInfo = buildWeightInfo(userId);

        // Recovery
        TodayVO.RecoveryInfo recoveryInfo = buildRecoveryInfo(userId, today);

        // Streak
        StreakVO streak = statsService.getStreak(userId);

        return TodayVO.builder()
                .greeting(greeting)
                .userName(userName)
                .date(today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")))
                .workout(workoutInfo)
                .weeklyActivity(weeklyActivity)
                .weight(weightInfo)
                .recovery(recoveryInfo)
                .streak(streak)
                .build();
    }

    private String buildGreeting() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 18) return "Good afternoon";
        return "Good evening";
    }

    private TodayVO.WorkoutInfo buildWorkoutInfo(List<WorkoutPlan> plans) {
        if (plans.isEmpty()) {
            return TodayVO.WorkoutInfo.builder().hasWorkout(false).build();
        }

        WorkoutPlan plan = plans.get(0);
        List<WorkoutPlanExercise> planExercises = planExerciseService.list(
                new LambdaQueryWrapper<WorkoutPlanExercise>()
                        .eq(WorkoutPlanExercise::getPlanId, plan.getId()));

        List<Long> exerciseIds = planExercises.stream()
                .map(WorkoutPlanExercise::getExerciseId).collect(Collectors.toList());
        String muscleGroups = exerciseIds.isEmpty() ? "" : exerciseService.listByIds(exerciseIds).stream()
                .map(Exercise::getMuscleGroup)
                .distinct()
                .collect(Collectors.joining(" / "));

        return TodayVO.WorkoutInfo.builder()
                .planId(plan.getId())
                .name(plan.getName())
                .muscleGroups(muscleGroups)
                .estimatedDuration(plan.getEstimatedDuration())
                .hasWorkout(true)
                .build();
    }

    private List<Boolean> buildWeeklyActivity(Long userId, LocalDate today) {
        LocalDate weekStart = today.minusDays(6);
        Set<LocalDate> workoutDates = workoutService.list(new LambdaQueryWrapper<Workout>()
                        .eq(Workout::getUserId, userId)
                        .eq(Workout::getStatus, "completed")
                        .ge(Workout::getStartTime, weekStart.atStartOfDay()))
                .stream()
                .map(w -> w.getStartTime().toLocalDate())
                .collect(Collectors.toSet());

        List<Boolean> activity = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            activity.add(workoutDates.contains(today.minusDays(i)));
        }
        return activity;
    }

    private TodayVO.WeightInfo buildWeightInfo(Long userId) {
        List<BodyMeasurement> measurements = bodyMeasurementService.list(
                new LambdaQueryWrapper<BodyMeasurement>()
                        .eq(BodyMeasurement::getUserId, userId)
                        .orderByDesc(BodyMeasurement::getMeasuredAt));

        if (measurements.isEmpty()) {
            return TodayVO.WeightInfo.builder().current(null).change(null).build();
        }

        double current = measurements.get(0).getWeight() != null ? measurements.get(0).getWeight() : 0;
        Double change = null;
        if (measurements.size() >= 2 && measurements.get(1).getWeight() != null) {
            change = Math.round((current - measurements.get(1).getWeight()) * 10.0) / 10.0;
        }

        return TodayVO.WeightInfo.builder().current(current).change(change).build();
    }

    private TodayVO.RecoveryInfo buildRecoveryInfo(Long userId, LocalDate today) {
        boolean workedOutToday = workoutService.count(new LambdaQueryWrapper<Workout>()
                .eq(Workout::getUserId, userId)
                .eq(Workout::getStatus, "completed")
                .ge(Workout::getStartTime, today.atStartOfDay())
                .le(Workout::getStartTime, today.plusDays(1).atStartOfDay())) > 0;

        if (workedOutToday) {
            return TodayVO.RecoveryInfo.builder().status("Great job today!").suggestion("Rest well").build();
        }

        // Check recent workouts
        long recentWorkouts = workoutService.count(new LambdaQueryWrapper<Workout>()
                .eq(Workout::getUserId, userId)
                .eq(Workout::getStatus, "completed")
                .ge(Workout::getStartTime, today.minusDays(3).atStartOfDay()));

        if (recentWorkouts > 0) {
            return TodayVO.RecoveryInfo.builder()
                    .status("Recovery Day")
                    .suggestion("Walking / Stretching / Mobility")
                    .build();
        }

        return TodayVO.RecoveryInfo.builder()
                .status("Ready to train")
                .suggestion("Time to hit the gym!")
                .build();
    }
}
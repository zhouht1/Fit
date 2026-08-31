package com.fit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fit.entity.Exercise;
import com.fit.entity.Workout;
import com.fit.entity.WorkoutSet;
import com.fit.service.ExerciseService;
import com.fit.service.StatsService;
import com.fit.service.WorkoutService;
import com.fit.service.WorkoutSetService;
import com.fit.vo.PersonalRecordVO;
import com.fit.vo.ProgressiveOverloadVO;
import com.fit.vo.StreakVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final WorkoutService workoutService;
    private final WorkoutSetService workoutSetService;
    private final ExerciseService exerciseService;

    public StatsServiceImpl(WorkoutService workoutService, WorkoutSetService workoutSetService, ExerciseService exerciseService) {
        this.workoutService = workoutService;
        this.workoutSetService = workoutSetService;
        this.exerciseService = exerciseService;
    }

    @Override
    public List<PersonalRecordVO> getPersonalRecords(Long userId) {
        List<Workout> workouts = workoutService.list(new LambdaQueryWrapper<Workout>()
                .eq(Workout::getUserId, userId)
                .eq(Workout::getStatus, "completed"));

        Set<Long> workoutIds = workouts.stream().map(Workout::getId).collect(Collectors.toSet());
        if (workoutIds.isEmpty()) return List.of();

        List<WorkoutSet> allSets = workoutSetService.list(new LambdaQueryWrapper<WorkoutSet>()
                .in(WorkoutSet::getWorkoutId, workoutIds));

        // Group by exercise
        Map<Long, List<WorkoutSet>> byExercise = allSets.stream()
                .collect(Collectors.groupingBy(WorkoutSet::getExerciseId));

        Map<Long, Exercise> exercises = exerciseService.listByIds(byExercise.keySet()).stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));

        List<PersonalRecordVO> records = new ArrayList<>();
        for (var entry : byExercise.entrySet()) {
            List<WorkoutSet> sets = entry.getValue();
            double maxWeight = sets.stream().mapToDouble(WorkoutSet::getWeight).max().orElse(0);
            int maxReps = sets.stream().mapToInt(WorkoutSet::getReps).max().orElse(0);
            double maxVolume = sets.stream()
                    .mapToDouble(s -> s.getWeight() * s.getReps())
                    .max().orElse(0);

            Exercise exercise = exercises.get(entry.getKey());
            records.add(PersonalRecordVO.builder()
                    .exerciseId(entry.getKey())
                    .exerciseName(exercise != null ? exercise.getName() : "Unknown")
                    .maxWeight(maxWeight)
                    .maxReps(maxReps)
                    .maxVolume(maxVolume)
                    .achievedAt("")
                    .build());
        }

        records.sort((a, b) -> Double.compare(b.getMaxWeight(), a.getMaxWeight()));
        return records;
    }

    @Override
    public List<ProgressiveOverloadVO> getProgressiveOverload(Long userId) {
        List<Workout> workouts = workoutService.list(new LambdaQueryWrapper<Workout>()
                .eq(Workout::getUserId, userId)
                .eq(Workout::getStatus, "completed")
                .orderByDesc(Workout::getStartTime));

        if (workouts.size() < 2) return List.of();

        Workout current = workouts.get(0);
        Workout previous = workouts.get(1);

        List<WorkoutSet> currentSets = workoutSetService.list(new LambdaQueryWrapper<WorkoutSet>()
                .eq(WorkoutSet::getWorkoutId, current.getId()));
        List<WorkoutSet> previousSets = workoutSetService.list(new LambdaQueryWrapper<WorkoutSet>()
                .eq(WorkoutSet::getWorkoutId, previous.getId()));

        Set<Long> exerciseIds = new HashSet<>();
        currentSets.forEach(s -> exerciseIds.add(s.getExerciseId()));
        previousSets.forEach(s -> exerciseIds.add(s.getExerciseId()));

        Map<Long, Exercise> exercises = exerciseService.listByIds(exerciseIds).stream()
                .collect(Collectors.toMap(Exercise::getId, e -> e));

        List<ProgressiveOverloadVO> comparisons = new ArrayList<>();
        for (Long exerciseId : exerciseIds) {
            List<WorkoutSet> curSets = currentSets.stream()
                    .filter(s -> s.getExerciseId().equals(exerciseId)).collect(Collectors.toList());
            List<WorkoutSet> prevSets = previousSets.stream()
                    .filter(s -> s.getExerciseId().equals(exerciseId)).collect(Collectors.toList());

            if (curSets.isEmpty() || prevSets.isEmpty()) continue;

            double curAvgWeight = curSets.stream().mapToDouble(WorkoutSet::getWeight).average().orElse(0);
            double prevAvgWeight = prevSets.stream().mapToDouble(WorkoutSet::getWeight).average().orElse(0);
            int curAvgReps = (int) curSets.stream().mapToInt(WorkoutSet::getReps).average().orElse(0);
            int prevAvgReps = (int) prevSets.stream().mapToInt(WorkoutSet::getReps).average().orElse(0);

            String suggestion = buildSuggestion(prevAvgWeight, prevAvgReps, curAvgWeight, curAvgReps);

            comparisons.add(ProgressiveOverloadVO.builder()
                    .exerciseName(exercises.getOrDefault(exerciseId, new Exercise()).getName())
                    .previousWeight(Math.round(prevAvgWeight * 10.0) / 10.0)
                    .previousReps(prevAvgReps)
                    .currentWeight(Math.round(curAvgWeight * 10.0) / 10.0)
                    .currentReps(curAvgReps)
                    .suggestion(suggestion)
                    .build());
        }

        return comparisons;
    }

    private String buildSuggestion(double prevWeight, int prevReps, double curWeight, int curReps) {
        if (curWeight > prevWeight) {
            double pct = ((curWeight - prevWeight) / prevWeight) * 100;
            return String.format("New Best: +%.1f%% Weight", pct);
        }
        if (curReps > prevReps) {
            return String.format("New Best: +%d Rep", curReps - prevReps);
        }
        if (curWeight == prevWeight && curReps == prevReps) {
            return "Suggested: Consider increasing weight by 2.5kg";
        }
        return "Keep pushing!";
    }

    @Override
    public StreakVO getStreak(Long userId) {
        List<Workout> workouts = workoutService.list(new LambdaQueryWrapper<Workout>()
                .eq(Workout::getUserId, userId)
                .eq(Workout::getStatus, "completed")
                .orderByDesc(Workout::getStartTime));

        if (workouts.isEmpty()) {
            return StreakVO.builder().currentStreak(0).longestStreak(0).lastWorkoutDate(null).build();
        }

        Set<LocalDate> workoutDates = workouts.stream()
                .map(w -> w.getStartTime().toLocalDate())
                .collect(Collectors.toCollection(TreeSet::new));

        // Calculate current streak (from today backwards)
        int currentStreak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        List<LocalDate> sortedDates = new ArrayList<>(workoutDates);
        Collections.reverse(sortedDates); // Oldest first

        // Current streak from most recent date backwards
        List<LocalDate> descDates = new ArrayList<>(workoutDates);
        Collections.reverse(descDates); // Newest first

        if (!descDates.isEmpty()) {
            LocalDate streakDate = descDates.get(0);
            // Allow 1 day gap (recovery day)
            if (streakDate.equals(today) || streakDate.equals(today.minusDays(1))) {
                currentStreak = 1;
                LocalDate expected = streakDate;
                for (int i = 1; i < descDates.size(); i++) {
                    LocalDate next = descDates.get(i);
                    // Allow up to 1 recovery day between workouts
                    if (expected.minusDays(1).equals(next) || expected.minusDays(2).equals(next)) {
                        currentStreak++;
                        expected = next;
                    } else {
                        break;
                    }
                }
            }
        }

        // Calculate longest streak
        int longestStreak = 1;
        int tempStreak = 1;
        for (int i = 1; i < sortedDates.size(); i++) {
            long diff = sortedDates.get(i).toEpochDay() - sortedDates.get(i - 1).toEpochDay();
            if (diff <= 2) { // Allow 1 recovery day
                tempStreak++;
                longestStreak = Math.max(longestStreak, tempStreak);
            } else {
                tempStreak = 1;
            }
        }

        String lastDate = descDates.get(0).toString();

        return StreakVO.builder()
                .currentStreak(currentStreak)
                .longestStreak(Math.max(longestStreak, currentStreak))
                .lastWorkoutDate(lastDate)
                .build();
    }
}
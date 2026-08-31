package com.fit.config;

import com.fit.entity.Exercise;
import com.fit.service.ExerciseService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private final ExerciseService exerciseService;

    public DataInitializer(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (exerciseService.count() > 0) {
            return;
        }

        List<Exercise> exercises = List.of(
            create("Bench Press", "Chest", "Barbell", "Lie on a flat bench, grip the barbell slightly wider than shoulder-width, lower to chest, then press up."),
            create("Incline DB Press", "Chest", "Dumbbell", "Set bench to 30-45 degree incline, press dumbbells from shoulder level to full extension."),
            create("Lateral Raise", "Shoulders", "Dumbbell", "Stand with dumbbells at sides, raise arms out to sides until parallel to floor, lower with control."),
            create("Triceps Pushdown", "Arms", "Cable", "Attach straight bar to high pulley, push down until arms are fully extended, squeeze triceps."),
            create("Squat", "Legs", "Barbell", "Place barbell on upper back, squat down until thighs are parallel to floor, drive through heels to stand."),
            create("Deadlift", "Back", "Barbell", "Stand with feet hip-width, grip barbell, lift by extending hips and knees, keep back straight."),
            create("Lat Pulldown", "Back", "Cable", "Grip wide bar, pull down to upper chest, squeeze shoulder blades, return with control."),
            create("Barbell Row", "Back", "Barbell", "Bend forward at hips, pull barbell to lower chest, squeeze back muscles, lower with control."),
            create("Shoulder Press", "Shoulders", "Barbell", "Press barbell from front of shoulders to overhead, lock out arms, lower under control."),
            create("Biceps Curl", "Arms", "Barbell", "Stand holding barbell with underhand grip, curl weight up to shoulders, lower with control.")
        );

        exerciseService.saveBatch(exercises);
    }

    private Exercise create(String name, String muscleGroup, String equipment, String description) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setMuscleGroup(muscleGroup);
        exercise.setEquipment(equipment);
        exercise.setDescription(description);
        return exercise;
    }
}
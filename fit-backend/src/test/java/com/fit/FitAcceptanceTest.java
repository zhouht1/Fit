package com.fit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full end-to-end acceptance test for Fit MVP.
 * Walks through the complete user journey:
 * Register → Login → Onboarding → Generate Plan → Today →
 * Start Workout → Record Sets → Finish → History → Progress → PR
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FitAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static Long workoutId;

    private static final String USERNAME = "acceptance_" + System.currentTimeMillis();
    private static final String EMAIL = USERNAME + "@fit.com";
    private static final String PASSWORD = "accept123";

    // ==================== PHASE 2: Authentication ====================

    @Test
    @Order(1)
    @DisplayName("E2E-1: Register")
    void step1_register() throws Exception {
        String body = json("""
            {"username":"%s","email":"%s","password":"%s"}
            """, USERNAME, EMAIL, PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value(USERNAME))
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        token = node.get("data").get("token").asText();
        System.out.println("✓ Register: " + USERNAME);
    }

    @Test
    @Order(2)
    @DisplayName("E2E-2: Login")
    void step2_login() throws Exception {
        String body = json("""
            {"username":"%s","password":"%s"}
            """, USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        token = node.get("data").get("token").asText();
        System.out.println("✓ Login: token obtained");
    }

    @Test
    @Order(3)
    @DisplayName("E2E-3: Get current user")
    void step3_me() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(USERNAME));
        System.out.println("✓ /me: " + USERNAME);
    }

    // ==================== PHASE 3: Profile + Onboarding ====================

    @Test
    @Order(4)
    @DisplayName("E2E-4: Onboarding - set profile")
    void step4_onboarding() throws Exception {
        String body = """
            {"name":"Alex","age":28,"height":178.0,"weight":75.0,"gender":"male","fitnessGoal":"Build Muscle","trainingFrequency":4,"experience":"Intermediate"}
            """;

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Alex"))
                .andExpect(jsonPath("$.data.fitnessGoal").value("Build Muscle"));
        System.out.println("✓ Onboarding: profile set");
    }

    // ==================== PHASE 4: Exercise Library ====================

    @Test
    @Order(5)
    @DisplayName("E2E-5: Browse exercises")
    void step5_exercises() throws Exception {
        mockMvc.perform(get("/api/exercises").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(10)));
        System.out.println("✓ Exercises: 10 available");
    }

    // ==================== PHASE 5: Workout Plan ====================

    @Test
    @Order(6)
    @DisplayName("E2E-6: Generate workout plan")
    void step6_createPlan() throws Exception {
        String body = """
            {"name":"Push Day","goal":"Build Muscle","trainingDays":3,"estimatedDuration":52,"exercises":[{"exerciseId":1,"targetSets":3,"targetReps":"8-10","orderNum":1},{"exerciseId":2,"targetSets":3,"targetReps":"10","orderNum":2},{"exerciseId":3,"targetSets":3,"targetReps":"12","orderNum":3}]}
            """;

        mockMvc.perform(post("/api/workout-plans")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Push Day"))
                .andExpect(jsonPath("$.data.exercises", hasSize(3)));
        System.out.println("✓ Plan: Push Day created with 3 exercises");
    }

    // ==================== PHASE 11: Today ====================

    @Test
    @Order(7)
    @DisplayName("E2E-7: Today dashboard")
    void step7_today() throws Exception {
        mockMvc.perform(get("/api/today").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.greeting").exists())
                .andExpect(jsonPath("$.data.workout.hasWorkout").value(true))
                .andExpect(jsonPath("$.data.workout.name").value("Push Day"));
        System.out.println("✓ Today: dashboard loaded");
    }

    // ==================== PHASE 6: Workout Core ====================

    @Test
    @Order(8)
    @DisplayName("E2E-8: Start workout")
    void step8_startWorkout() throws Exception {
        String body = """
            {"name":"Push Day"}
            """;

        MvcResult result = mockMvc.perform(post("/api/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("in_progress"))
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        workoutId = node.get("data").get("id").asLong();
        System.out.println("✓ Start Workout: #" + workoutId);
    }

    @Test
    @Order(9)
    @DisplayName("E2E-9: Record Set 1 - Bench Press 60kg × 10")
    void step9_recordSet1() throws Exception {
        String body = """
            {"exerciseId":1,"weight":60.0,"reps":10}
            """;

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.setNumber").value(1))
                .andExpect(jsonPath("$.data.volume").value(600.0));
        System.out.println("✓ Set 1: Bench Press 60kg × 10 = 600kg");
    }

    @Test
    @Order(10)
    @DisplayName("E2E-10: Record Set 2 - Bench Press 60kg × 10")
    void step10_recordSet2() throws Exception {
        String body = """
            {"exerciseId":1,"weight":60.0,"reps":10}
            """;

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.setNumber").value(2));
        System.out.println("✓ Set 2: Bench Press 60kg × 10 = 600kg");
    }

    @Test
    @Order(11)
    @DisplayName("E2E-11: Record Set 3 - Bench Press 62.5kg × 10")
    void step11_recordSet3() throws Exception {
        String body = """
            {"exerciseId":1,"weight":62.5,"reps":10}
            """;

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.setNumber").value(3))
                .andExpect(jsonPath("$.data.volume").value(625.0));
        System.out.println("✓ Set 3: Bench Press 62.5kg × 10 = 625kg");
    }

    @Test
    @Order(12)
    @DisplayName("E2E-12: Finish workout")
    void step12_finishWorkout() throws Exception {
        mockMvc.perform(post("/api/workouts/" + workoutId + "/finish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("completed"))
                .andExpect(jsonPath("$.data.totalVolume").value(1825.0))
                .andExpect(jsonPath("$.data.totalSets").value(3))
                .andExpect(jsonPath("$.data.exerciseCount").value(1));
        System.out.println("✓ Finish: 1825kg total volume, 3 sets, 1 exercise");
    }

    // ==================== PHASE 8: Workout History ====================

    @Test
    @Order(13)
    @DisplayName("E2E-13: Workout history")
    void step13_history() throws Exception {
        mockMvc.perform(get("/api/workouts").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].status").value("completed"));
        System.out.println("✓ History: 1 completed workout");
    }

    // ==================== PHASE 9: Progress ====================

    @Test
    @Order(14)
    @DisplayName("E2E-14: Add body measurement")
    void step14_bodyMeasurement() throws Exception {
        String body = """
            {"weight":74.5,"bodyFat":17.5,"chest":100.0,"waist":82.0}
            """;

        mockMvc.perform(post("/api/body-measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.weight").value(74.5));
        System.out.println("✓ Body: weight 74.5 kg recorded");
    }

    @Test
    @Order(15)
    @DisplayName("E2E-15: Progress data")
    void step15_progress() throws Exception {
        mockMvc.perform(get("/api/progress?period=30d")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        System.out.println("✓ Progress: data loaded");
    }

    // ==================== PHASE 10: PR ====================

    @Test
    @Order(16)
    @DisplayName("E2E-16: Personal records")
    void step16_pr() throws Exception {
        mockMvc.perform(get("/api/stats/personal-records")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].exerciseName").value("Bench Press"))
                .andExpect(jsonPath("$.data[0].maxWeight").value(62.5))
                .andExpect(jsonPath("$.data[0].maxVolume").value(625.0));
        System.out.println("✓ PR: Bench Press max 62.5kg, 625kg volume");
    }

    @Test
    @Order(17)
    @DisplayName("E2E-17: Streak")
    void step17_streak() throws Exception {
        mockMvc.perform(get("/api/stats/streak")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStreak").exists());
        System.out.println("✓ Streak: calculated");
    }

    // ==================== Helper ====================

    private String json(String template, String... args) {
        return String.format(template, (Object[]) args);
    }
}
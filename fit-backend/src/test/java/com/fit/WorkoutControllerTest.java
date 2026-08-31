package com.fit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static Long workoutId;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        String username = "workouttest_" + System.currentTimeMillis();
        String body = """
            {"username":"%s","email":"%s@fit.com","password":"test123"}
            """.formatted(username, username);

        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        var node = objectMapper.readTree(result.getResponse().getContentAsString());
        token = node.get("data").get("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("POST /workouts - start workout")
    void testStartWorkout() throws Exception {
        String body = """
            {"name": "Push Day", "planId": null}
            """;

        var result = mockMvc.perform(post("/api/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Push Day"))
                .andExpect(jsonPath("$.data.status").value("in_progress"))
                .andExpect(jsonPath("$.data.startTime").exists())
                .andReturn();

        var node = objectMapper.readTree(result.getResponse().getContentAsString());
        workoutId = node.get("data").get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("POST /workouts/{id}/sets - add set 1 (Bench Press 60kg x 10)")
    void testAddSet1() throws Exception {
        String body = """
            {"exerciseId": 1, "weight": 60.0, "reps": 10}
            """;

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.setNumber").value(1))
                .andExpect(jsonPath("$.data.weight").value(60.0))
                .andExpect(jsonPath("$.data.reps").value(10))
                .andExpect(jsonPath("$.data.volume").value(600.0))
                .andExpect(jsonPath("$.data.exerciseName").value("Bench Press"));
    }

    @Test
    @Order(3)
    @DisplayName("POST /workouts/{id}/sets - add set 2 (Bench Press 60kg x 10)")
    void testAddSet2() throws Exception {
        String body = """
            {"exerciseId": 1, "weight": 60.0, "reps": 10}
            """;

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.setNumber").value(2));
    }

    @Test
    @Order(4)
    @DisplayName("POST /workouts/{id}/sets - add set 3 (Bench Press 62.5kg x 10)")
    void testAddSet3() throws Exception {
        String body = """
            {"exerciseId": 1, "weight": 62.5, "reps": 10}
            """;

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.setNumber").value(3))
                .andExpect(jsonPath("$.data.volume").value(625.0));
    }

    @Test
    @Order(5)
    @DisplayName("GET /workouts/{id} - check workout with sets")
    void testGetWorkoutWithSets() throws Exception {
        mockMvc.perform(get("/api/workouts/" + workoutId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("in_progress"))
                .andExpect(jsonPath("$.data.sets", hasSize(3)))
                .andExpect(jsonPath("$.data.totalVolume").value(1825.0))
                .andExpect(jsonPath("$.data.totalSets").value(3));
    }

    @Test
    @Order(6)
    @DisplayName("POST /workouts/{id}/finish - finish workout")
    void testFinishWorkout() throws Exception {
        mockMvc.perform(post("/api/workouts/" + workoutId + "/finish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("completed"))
                .andExpect(jsonPath("$.data.endTime").exists())
                .andExpect(jsonPath("$.data.totalVolume").value(1825.0))
                .andExpect(jsonPath("$.data.totalSets").value(3))
                .andExpect(jsonPath("$.data.exerciseCount").value(1));
    }

    @Test
    @Order(7)
    @DisplayName("POST /workouts/{id}/sets - add set to finished workout should fail")
    void testAddSetToFinished() throws Exception {
        String body = """
            {"exerciseId": 1, "weight": 60.0, "reps": 10}
            """;

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Workout is already finished"));
    }

    @Test
    @Order(8)
    @DisplayName("GET /workouts - list workouts")
    void testGetWorkouts() throws Exception {
        mockMvc.perform(get("/api/workouts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].status").value("completed"));
    }

    @Test
    @Order(9)
    @DisplayName("Unauthorized access")
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/workouts"))
                .andExpect(status().isForbidden());
    }
}
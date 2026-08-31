package com.fit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        String username = "statstest_" + System.currentTimeMillis();
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

        // Create a workout with sets to generate PRs
        var wResult = mockMvc.perform(post("/api/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Push Day\"}"))
                .andExpect(status().isOk())
                .andReturn();

        var wNode = objectMapper.readTree(wResult.getResponse().getContentAsString());
        Long workoutId = wNode.get("data").get("id").asLong();

        // Add sets
        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseId\": 1, \"weight\": 60.0, \"reps\": 10}"));

        mockMvc.perform(post("/api/workouts/" + workoutId + "/sets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseId\": 1, \"weight\": 62.5, \"reps\": 10}"));

        // Finish workout
        mockMvc.perform(post("/api/workouts/" + workoutId + "/finish")
                .header("Authorization", "Bearer " + token));
    }

    @Test
    @Order(1)
    @DisplayName("GET /stats/personal-records")
    void testPersonalRecords() throws Exception {
        mockMvc.perform(get("/api/stats/personal-records")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].exerciseName").value("Bench Press"))
                .andExpect(jsonPath("$.data[0].maxWeight").value(62.5))
                .andExpect(jsonPath("$.data[0].maxReps").value(10))
                .andExpect(jsonPath("$.data[0].maxVolume").value(625.0));
    }

    @Test
    @Order(2)
    @DisplayName("GET /stats/progressive-overload - no previous workout")
    void testProgressiveOverload() throws Exception {
        mockMvc.perform(get("/api/stats/progressive-overload")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(3)
    @DisplayName("GET /stats/streak")
    void testStreak() throws Exception {
        mockMvc.perform(get("/api/stats/streak")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.currentStreak").exists())
                .andExpect(jsonPath("$.data.longestStreak").exists());
    }

    @Test
    @Order(4)
    @DisplayName("GET /stats - unauthorized")
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/stats/streak"))
                .andExpect(status().isForbidden());
    }
}
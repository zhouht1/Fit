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
class TodayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        String username = "todaytest_" + System.currentTimeMillis();
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

        // Set up profile
        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test\", \"fitnessGoal\": \"Build Muscle\"}"));

        // Create a plan
        mockMvc.perform(post("/api/workout-plans")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Push Day", "goal": "Build Muscle", "exercises": [
                        {"exerciseId": 1, "targetSets": 3, "targetReps": "8-10", "orderNum": 1}
                    ]}
                    """));
    }

    @Test
    @Order(1)
    @DisplayName("GET /today - should return full dashboard")
    void testGetToday() throws Exception {
        mockMvc.perform(get("/api/today")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.greeting").exists())
                .andExpect(jsonPath("$.data.userName").value("Test"))
                .andExpect(jsonPath("$.data.date").exists())
                .andExpect(jsonPath("$.data.workout.hasWorkout").value(true))
                .andExpect(jsonPath("$.data.workout.name").value("Push Day"))
                .andExpect(jsonPath("$.data.weeklyActivity").isArray())
                .andExpect(jsonPath("$.data.recovery").exists())
                .andExpect(jsonPath("$.data.streak").exists());
    }

    @Test
    @Order(2)
    @DisplayName("GET /today - unauthorized")
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/today"))
                .andExpect(status().isForbidden());
    }
}
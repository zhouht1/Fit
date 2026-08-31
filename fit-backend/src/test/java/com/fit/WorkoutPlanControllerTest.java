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
class WorkoutPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static Long planId;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        String username = "plantest_" + System.currentTimeMillis();
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
    @DisplayName("POST /workout-plans - create plan with exercises")
    void testCreatePlan() throws Exception {
        String body = """
            {
                "name": "Push Day",
                "goal": "Build Muscle",
                "trainingDays": 3,
                "estimatedDuration": 60,
                "exercises": [
                    {"exerciseId": 1, "targetSets": 3, "targetReps": "8-10", "orderNum": 1},
                    {"exerciseId": 2, "targetSets": 3, "targetReps": "10", "orderNum": 2},
                    {"exerciseId": 3, "targetSets": 3, "targetReps": "12", "orderNum": 3}
                ]
            }
            """;

        var result = mockMvc.perform(post("/api/workout-plans")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Push Day"))
                .andExpect(jsonPath("$.data.exercises", hasSize(3)))
                .andExpect(jsonPath("$.data.exercises[0].exerciseName").exists())
                .andReturn();

        var node = objectMapper.readTree(result.getResponse().getContentAsString());
        planId = node.get("data").get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("GET /workout-plans - list all plans")
    void testGetPlans() throws Exception {
        mockMvc.perform(get("/api/workout-plans")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Push Day"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /workout-plans/{id} - get plan detail")
    void testGetPlanById() throws Exception {
        mockMvc.perform(get("/api/workout-plans/" + planId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Push Day"))
                .andExpect(jsonPath("$.data.exercises", hasSize(3)));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /workout-plans/{id} - update plan")
    void testUpdatePlan() throws Exception {
        String body = """
            {
                "name": "Push Day Updated",
                "goal": "Get Stronger",
                "trainingDays": 4,
                "estimatedDuration": 50,
                "exercises": [
                    {"exerciseId": 1, "targetSets": 4, "targetReps": "6-8", "orderNum": 1}
                ]
            }
            """;

        mockMvc.perform(put("/api/workout-plans/" + planId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Push Day Updated"))
                .andExpect(jsonPath("$.data.goal").value("Get Stronger"))
                .andExpect(jsonPath("$.data.exercises", hasSize(1)));
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /workout-plans/{id} - delete plan")
    void testDeletePlan() throws Exception {
        mockMvc.perform(delete("/api/workout-plans/" + planId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(6)
    @DisplayName("GET /workout-plans/{id} - deleted plan should return 404")
    void testGetDeletedPlan() throws Exception {
        mockMvc.perform(get("/api/workout-plans/" + planId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(7)
    @DisplayName("POST /workout-plans - validation error")
    void testValidationError() throws Exception {
        String body = """
            {"name": "", "goal": "Test"}
            """;

        mockMvc.perform(post("/api/workout-plans")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(8)
    @DisplayName("User isolation - other user cannot access plan")
    void testUserIsolation() throws Exception {
        // Register another user
        String username2 = "plantest2_" + System.currentTimeMillis();
        String body = """
            {"username":"%s","email":"%s@fit.com","password":"test123"}
            """.formatted(username2, username2);

        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        var node = objectMapper.readTree(result.getResponse().getContentAsString());
        String token2 = node.get("data").get("token").asText();

        // Create plan for user 2
        String planBody = """
            {"name": "User2 Plan", "goal": "Test", "exercises": []}
            """;
        var planResult = mockMvc.perform(post("/api/workout-plans")
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(planBody))
                .andExpect(status().isOk())
                .andReturn();

        var planNode = objectMapper.readTree(planResult.getResponse().getContentAsString());
        Long user2PlanId = planNode.get("data").get("id").asLong();

        // User 1 tries to access User 2's plan
        mockMvc.perform(get("/api/workout-plans/" + user2PlanId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }
}
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
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        String username = "progresstest_" + System.currentTimeMillis();
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
    @DisplayName("POST /body-measurements - add measurement")
    void testAddMeasurement() throws Exception {
        String body = """
            {"weight": 72.5, "bodyFat": 18.0, "chest": 98.0, "waist": 82.0, "hip": 95.0, "arm": 34.0, "thigh": 55.0, "note": "Morning measurement"}
            """;

        mockMvc.perform(post("/api/body-measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.weight").value(72.5))
                .andExpect(jsonPath("$.data.bodyFat").value(18.0))
                .andExpect(jsonPath("$.data.measuredAt").exists());
    }

    @Test
    @Order(2)
    @DisplayName("GET /body-measurements - list measurements")
    void testGetMeasurements() throws Exception {
        mockMvc.perform(get("/api/body-measurements")
                        .param("period", "30d")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].weight").value(72.5));
    }

    @Test
    @Order(3)
    @DisplayName("GET /progress - get progress data")
    void testGetProgress() throws Exception {
        mockMvc.perform(get("/api/progress")
                        .param("period", "30d")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(4)
    @DisplayName("GET /body-measurements - validation error")
    void testValidationError() throws Exception {
        String body = """
            {"weight": 10}
            """;

        mockMvc.perform(post("/api/body-measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(5)
    @DisplayName("GET /progress - unauthorized")
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/progress"))
                .andExpect(status().isForbidden());
    }
}
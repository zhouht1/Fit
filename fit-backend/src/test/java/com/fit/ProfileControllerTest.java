package com.fit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.dto.ProfileRequest;
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
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // Register a user first
        String username = "profiletest_" + System.currentTimeMillis();
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
    @DisplayName("GET /profile - empty profile should return null")
    void testGetEmptyProfile() throws Exception {
        mockMvc.perform(get("/api/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @Order(2)
    @DisplayName("PUT /profile - create/update profile should succeed")
    void testUpdateProfile() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setName("Test User");
        request.setAge(25);
        request.setHeight(175.0);
        request.setWeight(70.0);
        request.setGender("male");
        request.setFitnessGoal("Build Muscle");
        request.setTrainingFrequency(4);
        request.setExperience("Intermediate");

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.age").value(25))
                .andExpect(jsonPath("$.data.fitnessGoal").value("Build Muscle"))
                .andExpect(jsonPath("$.data.trainingFrequency").value(4))
                .andExpect(jsonPath("$.data.experience").value("Intermediate"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /profile - should return updated profile")
    void testGetProfile() throws Exception {
        mockMvc.perform(get("/api/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.age").value(25));
    }

    @Test
    @Order(4)
    @DisplayName("PUT /profile - partial update should work")
    void testPartialUpdate() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setWeight(68.0);
        request.setFitnessGoal("Lose Fat");

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.weight").value(68.0))
                .andExpect(jsonPath("$.data.fitnessGoal").value("Lose Fat"))
                .andExpect(jsonPath("$.data.name").value("Test User"));
    }

    @Test
    @Order(5)
    @DisplayName("GET /profile - user isolation (unauthorized)")
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @DisplayName("PUT /profile - validation error")
    void testValidationError() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setAge(5); // too young
        request.setHeight(10.0); // too short

        mockMvc.perform(put("/api/profile")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
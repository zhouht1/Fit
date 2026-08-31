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
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @BeforeAll
    static void setUp(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        String username = "exercisetest_" + System.currentTimeMillis();
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
    @DisplayName("GET /exercises - should return all 10 exercises")
    void testGetAllExercises() throws Exception {
        mockMvc.perform(get("/api/exercises")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(10)))
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].muscleGroup").exists())
                .andExpect(jsonPath("$.data[0].equipment").exists());
    }

    @Test
    @Order(2)
    @DisplayName("GET /exercises?keyword=Bench - should filter by name")
    void testSearchByKeyword() throws Exception {
        mockMvc.perform(get("/api/exercises")
                        .param("keyword", "Bench")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].name", containsString("Bench")));
    }

    @Test
    @Order(3)
    @DisplayName("GET /exercises?muscleGroup=Chest - should filter by muscle group")
    void testFilterByMuscleGroup() throws Exception {
        mockMvc.perform(get("/api/exercises")
                        .param("muscleGroup", "Chest")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].muscleGroup", is("Chest")));
    }

    @Test
    @Order(4)
    @DisplayName("GET /exercises/1 - should return exercise details")
    void testGetExerciseById() throws Exception {
        mockMvc.perform(get("/api/exercises/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.description").exists());
    }

    @Test
    @Order(5)
    @DisplayName("GET /exercises/999 - should return 404")
    void testGetNonExistentExercise() throws Exception {
        mockMvc.perform(get("/api/exercises/999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Exercise not found"));
    }

    @Test
    @Order(6)
    @DisplayName("GET /exercises - unauthorized should fail")
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isForbidden());
    }
}
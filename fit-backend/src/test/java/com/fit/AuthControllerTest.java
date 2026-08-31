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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static final String TEST_USERNAME = "authtest_" + System.currentTimeMillis();
    private static final String TEST_EMAIL = TEST_USERNAME + "@fit.com";
    private static final String TEST_PASSWORD = "test123";

    @Test
    @Order(1)
    @DisplayName("Register - should succeed")
    void testRegister() throws Exception {
        String body = """
            {"username":"%s","email":"%s","password":"%s"}
            """.formatted(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        token = node.get("data").get("token").asText();
    }

    @Test
    @Order(2)
    @DisplayName("Register - duplicate should fail")
    void testDuplicateRegister() throws Exception {
        String body = """
            {"username":"%s","email":"%s","password":"%s"}
            """.formatted(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    @Order(3)
    @DisplayName("Login - should succeed")
    void testLogin() throws Exception {
        String body = """
            {"username":"%s","password":"%s"}
            """.formatted(TEST_USERNAME, TEST_PASSWORD);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        token = node.get("data").get("token").asText();
    }

    @Test
    @Order(4)
    @DisplayName("Login - wrong password should fail")
    void testWrongPassword() throws Exception {
        String body = """
            {"username":"%s","password":"wrongpassword"}
            """.formatted(TEST_USERNAME);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(5)
    @DisplayName("GET /me - with valid token should succeed")
    void testMeWithToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value(TEST_USERNAME));
    }

    @Test
    @Order(6)
    @DisplayName("GET /me - without token should fail")
    void testMeWithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    @DisplayName("Register - validation errors")
    void testValidationErrors() throws Exception {
        String body = """
            {"username":"","email":"bad","password":"12"}
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
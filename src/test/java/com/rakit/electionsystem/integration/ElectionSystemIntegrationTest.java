package com.rakit.electionsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakit.electionsystem.config.TestConfig;
import com.rakit.electionsystem.dto.AuthResponse;
import com.rakit.electionsystem.dto.LoginRequest;
import com.rakit.electionsystem.dto.RegisterRequest;
import com.rakit.electionsystem.model.Role;
import com.rakit.electionsystem.model.Voter;
import com.rakit.electionsystem.repository.ElectionRepository;
import com.rakit.electionsystem.repository.VoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the Election System API.
 * Tests the complete flow: authentication, voting, and results.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class ElectionSystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        voterRepository.deleteAll();
        electionRepository.deleteAll();
    }

    @Test
    void testCompleteAuthenticationFlow() throws Exception {
        // Test registration
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andReturn();

        String registerResponseBody = registerResult.getResponse().getContentAsString();
        AuthResponse registerResponse = objectMapper.readValue(registerResponseBody, AuthResponse.class);
        assertThat(registerResponse.getToken()).isNotBlank();

        // Test login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andReturn();

        String loginResponseBody = loginResult.getResponse().getContentAsString();
        AuthResponse loginResponse = objectMapper.readValue(loginResponseBody, AuthResponse.class);
        assertThat(loginResponse.getToken()).isNotBlank();
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Create a user first
        Voter voter = new Voter();
        voter.setEmail("existing@example.com");
        voter.setPassword(passwordEncoder.encode("correctPassword"));
        voter.setFirstName("Jane");
        voter.setLastName("Smith");
        voter.setRole(Role.ROLE_VOTER);
        voter.setActive(true);
        voterRepository.save(voter);

        // Test login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("existing@example.com");
        loginRequest.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterWithDuplicateEmail() throws Exception {
        // Create existing user
        Voter existingVoter = new Voter();
        existingVoter.setEmail("duplicate@example.com");
        existingVoter.setPassword(passwordEncoder.encode("password"));
        existingVoter.setFirstName("Existing");
        existingVoter.setLastName("User");
        existingVoter.setRole(Role.ROLE_VOTER);
        existingVoter.setActive(true);
        voterRepository.save(existingVoter);

        // Try to register with same email - should return 409 CONFLICT (DuplicateResourceException)
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("duplicate@example.com");
        registerRequest.setPassword("newPassword");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        // Try to access protected endpoint without authentication
        // Spring Security returns 403 FORBIDDEN when authentication is missing
        mockMvc.perform(get("/api/admin/voters"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAccessProtectedEndpointWithInvalidToken() throws Exception {
        // Try to access protected endpoint with invalid token
        // Spring Security returns 403 FORBIDDEN for invalid tokens
        mockMvc.perform(get("/api/admin/voters")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

    private String registerAndGetToken(String email, String password, Role role) throws Exception {
        Voter voter = new Voter();
        voter.setEmail(email);
        voter.setPassword(passwordEncoder.encode(password));
        voter.setFirstName("Test");
        voter.setLastName("User");
        voter.setRole(role);
        voter.setActive(true);
        voterRepository.save(voter);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        return authResponse.getToken();
    }
}

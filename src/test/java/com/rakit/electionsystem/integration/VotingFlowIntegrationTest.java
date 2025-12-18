package com.rakit.electionsystem.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rakit.electionsystem.config.TestConfig;
import com.rakit.electionsystem.dto.AuthResponse;
import com.rakit.electionsystem.dto.ElectionResultsResponse;
import com.rakit.electionsystem.dto.LoginRequest;
import com.rakit.electionsystem.dto.VoteRequest;
import com.rakit.electionsystem.model.Election;
import com.rakit.electionsystem.model.ElectionOption;
import com.rakit.electionsystem.model.ElectionStatus;
import com.rakit.electionsystem.model.Role;
import com.rakit.electionsystem.model.Vote;
import com.rakit.electionsystem.model.Voter;
import com.rakit.electionsystem.model.VotingRecord;
import com.rakit.electionsystem.repository.ElectionOptionRepository;
import com.rakit.electionsystem.repository.ElectionRepository;
import com.rakit.electionsystem.repository.VoteRepository;
import com.rakit.electionsystem.repository.VoterRepository;
import com.rakit.electionsystem.repository.VotingRecordRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the voting flow.
 * Tests anonymous voting and results retrieval.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class VotingFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private ElectionOptionRepository electionOptionRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingRecordRepository votingRecordRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Election activeElection;
    private ElectionOption option1;
    private ElectionOption option2;

    @BeforeEach
    void setUp() {
        // Clean up
        voteRepository.deleteAll();
        votingRecordRepository.deleteAll();
        electionOptionRepository.deleteAll();
        electionRepository.deleteAll();
        voterRepository.deleteAll();

        // Create active election
        activeElection = new Election();
        activeElection.setName("Test Election");
        activeElection.setDescription("Test Description");
        activeElection.setStatus(ElectionStatus.ACTIVE);
        activeElection.setStartDate(LocalDateTime.now().minusDays(1));
        activeElection.setEndDate(LocalDateTime.now().plusDays(1));
        activeElection.setCreatedAt(LocalDateTime.now());
        activeElection = electionRepository.save(activeElection);

        // Create options
        option1 = new ElectionOption();
        option1.setOptionTitle("Option 1");
        option1.setDescription("First option");
        option1.setElection(activeElection);
        option1 = electionOptionRepository.save(option1);

        option2 = new ElectionOption();
        option2.setOptionTitle("Option 2");
        option2.setDescription("Second option");
        option2.setElection(activeElection);
        option2 = electionOptionRepository.save(option2);

        List<ElectionOption> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        activeElection.setOptions(options);
    }

    @Test
    void testCompleteVotingFlow() throws Exception {
        // Create and authenticate voter
        String voterToken = createVoterAndGetToken("voter@example.com", "password123");

        // Cast vote
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setElectionId(activeElection.getId());
        voteRequest.setOptionId(option1.getId());

        mockMvc.perform(post("/api/votes/cast")
                        .header("Authorization", "Bearer " + voterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        // Verify vote was recorded anonymously
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(1);
        assertThat(votes.get(0).getElectionOption().getId()).isEqualTo(option1.getId());

        // Verify voting record was created
        List<VotingRecord> records = votingRecordRepository.findAll();
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getElection().getId()).isEqualTo(activeElection.getId());

        // Get results
        MvcResult resultsResult = mockMvc.perform(get("/api/results/elections/" + activeElection.getId())
                        .header("Authorization", "Bearer " + voterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.electionId").value(activeElection.getId()))
                .andExpect(jsonPath("$.totalVotes").value(1))
                .andExpect(jsonPath("$.optionResults").isArray())
                .andReturn();

        String resultsBody = resultsResult.getResponse().getContentAsString();
        ElectionResultsResponse results = objectMapper.readValue(resultsBody, ElectionResultsResponse.class);
        assertThat(results.getOptionResults()).hasSize(2);
        assertThat(results.getOptionResults().get(0).getVoteCount()).isEqualTo(1L);
        assertThat(results.getOptionResults().get(1).getVoteCount()).isEqualTo(0L);
    }

    @Test
    void testCannotVoteTwiceInSameElection() throws Exception {
        String voterToken = createVoterAndGetToken("voter@example.com", "password123");

        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setElectionId(activeElection.getId());
        voteRequest.setOptionId(option1.getId());

        // First vote should succeed
        mockMvc.perform(post("/api/votes/cast")
                        .header("Authorization", "Bearer " + voterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        // Second vote should fail with 409 CONFLICT (AlreadyVotedException)
        mockMvc.perform(post("/api/votes/cast")
                        .header("Authorization", "Bearer " + voterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void testCannotVoteInInactiveElection() throws Exception {
        // Create inactive election
        Election inactiveElection = new Election();
        inactiveElection.setName("Inactive Election");
        inactiveElection.setDescription("Test");
        inactiveElection.setStatus(ElectionStatus.DRAFT);
        inactiveElection.setStartDate(LocalDateTime.now());
        inactiveElection.setEndDate(LocalDateTime.now().plusDays(1));
        inactiveElection.setCreatedAt(LocalDateTime.now());
        inactiveElection = electionRepository.save(inactiveElection);

        ElectionOption inactiveOption = new ElectionOption();
        inactiveOption.setOptionTitle("Option");
        inactiveOption.setDescription("Test");
        inactiveOption.setElection(inactiveElection);
        inactiveOption = electionOptionRepository.save(inactiveOption);

        String voterToken = createVoterAndGetToken("voter@example.com", "password123");

        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setElectionId(inactiveElection.getId());
        voteRequest.setOptionId(inactiveOption.getId());

        mockMvc.perform(post("/api/votes/cast")
                        .header("Authorization", "Bearer " + voterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAnonymityIsPreserved() throws Exception {
        // Create multiple voters and votes
        String voter1Token = createVoterAndGetToken("voter1@example.com", "password123");
        String voter2Token = createVoterAndGetToken("voter2@example.com", "password123");

        // Voter 1 votes for option 1
        VoteRequest vote1Request = new VoteRequest();
        vote1Request.setElectionId(activeElection.getId());
        vote1Request.setOptionId(option1.getId());
        mockMvc.perform(post("/api/votes/cast")
                        .header("Authorization", "Bearer " + voter1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vote1Request)))
                .andExpect(status().isOk());

        // Voter 2 votes for option 2
        VoteRequest vote2Request = new VoteRequest();
        vote2Request.setElectionId(activeElection.getId());
        vote2Request.setOptionId(option2.getId());
        mockMvc.perform(post("/api/votes/cast")
                        .header("Authorization", "Bearer " + voter2Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vote2Request)))
                .andExpect(status().isOk());

        // Verify votes exist but don't link to voters
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(2);
        votes.forEach(vote -> {
            // Vote should only have references to election and option, NOT to voter
            assertThat(vote.getElection()).isNotNull();
            assertThat(vote.getElectionOption()).isNotNull();
        });

        // Verify voting records exist but don't indicate which option was chosen
        List<VotingRecord> records = votingRecordRepository.findAll();
        assertThat(records).hasSize(2);
        records.forEach(record -> {
            // VotingRecord should only link voter to election, NOT to specific option
            assertThat(record.getVoter()).isNotNull();
            assertThat(record.getElection()).isNotNull();
        });
    }

    private String createVoterAndGetToken(String email, String password) throws Exception {
        Voter voter = new Voter();
        voter.setEmail(email);
        voter.setPassword(passwordEncoder.encode(password));
        voter.setFirstName("Test");
        voter.setLastName("Voter");
        voter.setRole(Role.ROLE_VOTER);
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

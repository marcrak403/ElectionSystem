package com.rakit.electionsystem.service;

import com.rakit.electionsystem.dto.ElectionResultsResponse;
import com.rakit.electionsystem.dto.ElectionStatisticsResponse;
import com.rakit.electionsystem.dto.OptionResultResponse;
import com.rakit.electionsystem.exception.ResourceNotFoundException;
import com.rakit.electionsystem.model.Election;
import com.rakit.electionsystem.model.ElectionOption;
import com.rakit.electionsystem.model.ElectionStatus;
import com.rakit.electionsystem.repository.ElectionRepository;
import com.rakit.electionsystem.repository.VoteRepository;
import com.rakit.electionsystem.repository.VotingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ResultsService.
 */
@ExtendWith(MockitoExtension.class)
class ResultsServiceTest {

    @Mock
    private ElectionRepository electionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingRecordRepository votingRecordRepository;

    @InjectMocks
    private ResultsService resultsService;

    private Election election;
    
    @BeforeEach
    void setUp() {
        // Create test election
        election = new Election();
        election.setId(1L);
        election.setName("Test Election");
        election.setDescription("Test Description");
        election.setStatus(ElectionStatus.ACTIVE);
        election.setStartDate(LocalDateTime.now().minusDays(1));
        election.setEndDate(LocalDateTime.now().plusDays(1));
        election.setCreatedAt(LocalDateTime.now());

        // Create test options
        ElectionOption option1 = new ElectionOption();
        option1.setId(1L);
        option1.setOptionTitle("Option 1");
        option1.setDescription("First option");
        option1.setElection(election);
        
        ElectionOption option2 = new ElectionOption();
        option2.setId(2L);
        option2.setOptionTitle("Option 2");
        option2.setDescription("Second option");
        option2.setElection(election);

        List<ElectionOption> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        election.setOptions(options);
    }

    @Test
    void getElectionResults_ShouldReturnResults_WhenElectionExists() {
        // Given
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(voteRepository.countByElectionIdAndElectionOptionId(1L, 1L)).thenReturn(10L);
        when(voteRepository.countByElectionIdAndElectionOptionId(1L, 2L)).thenReturn(5L);
        when(voteRepository.countByElectionId(1L)).thenReturn(15L);

        // When
        ElectionResultsResponse results = resultsService.getElectionResults(1L);

        // Then
        assertThat(results).isNotNull();
        assertThat(results.getElectionId()).isEqualTo(1L);
        assertThat(results.getName()).isEqualTo("Test Election");
        assertThat(results.getTotalVotes()).isEqualTo(15L);
        assertThat(results.getOptionResults()).hasSize(2);

        OptionResultResponse result1 = results.getOptionResults().get(0);
        assertThat(result1.getOptionId()).isEqualTo(1L);
        assertThat(result1.getVoteCount()).isEqualTo(10L);

        OptionResultResponse result2 = results.getOptionResults().get(1);
        assertThat(result2.getOptionId()).isEqualTo(2L);
        assertThat(result2.getVoteCount()).isEqualTo(5L);
    }

    @Test
    void getElectionResults_ShouldThrowException_WhenElectionNotFound() {
        // Given
        when(electionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> resultsService.getElectionResults(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Election not found");
    }

    @Test
    void getOptionVoteCount_ShouldReturnCount() {
        // Given
        when(voteRepository.countByElectionOptionId(1L)).thenReturn(10L);

        // When
        Long voteCount = resultsService.getOptionVoteCount(1L);

        // Then
        assertThat(voteCount).isEqualTo(10L);
    }

    @Test
    void getElectionStatistics_ShouldReturnStatistics_WhenElectionExists() {
        // Given
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(voteRepository.countByElectionId(1L)).thenReturn(15L);
        when(votingRecordRepository.countByElectionId(1L)).thenReturn(20L);

        // When
        ElectionStatisticsResponse statistics = resultsService.getElectionStatistics(1L);

        // Then
        assertThat(statistics).isNotNull();
        assertThat(statistics.getElectionId()).isEqualTo(1L);
        assertThat(statistics.getName()).isEqualTo("Test Election");
        assertThat(statistics.getTotalVotes()).isEqualTo(15L);
        assertThat(statistics.getTotalParticipants()).isEqualTo(20L);
        assertThat(statistics.getTotalOptions()).isEqualTo(2L);
        assertThat(statistics.getParticipationRate()).isEqualTo(75.0); // 15/20 * 100
    }

    @Test
    void getElectionStatistics_ShouldHandleZeroParticipants() {
        // Given
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(voteRepository.countByElectionId(1L)).thenReturn(0L);
        when(votingRecordRepository.countByElectionId(1L)).thenReturn(0L);

        // When
        ElectionStatisticsResponse statistics = resultsService.getElectionStatistics(1L);

        // Then
        assertThat(statistics).isNotNull();
        assertThat(statistics.getParticipationRate()).isEqualTo(0.0);
    }

    @Test
    void getElectionStatistics_ShouldThrowException_WhenElectionNotFound() {
        // Given
        when(electionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> resultsService.getElectionStatistics(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Election not found");
    }
}

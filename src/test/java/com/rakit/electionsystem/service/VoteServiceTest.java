package com.rakit.electionsystem.service;

import com.rakit.electionsystem.dto.VoteRequest;
import com.rakit.electionsystem.exception.AlreadyVotedException;
import com.rakit.electionsystem.exception.ElectionNotActiveException;
import com.rakit.electionsystem.exception.ResourceNotFoundException;
import com.rakit.electionsystem.model.Election;
import com.rakit.electionsystem.model.ElectionOption;
import com.rakit.electionsystem.model.ElectionStatus;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VoteService.
 */
@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoterRepository voterRepository;

    @Mock
    private ElectionRepository electionRepository;

    @Mock
    private ElectionOptionRepository electionOptionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingRecordRepository votingRecordRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private VoteService voteService;

    private Voter voter;
    private Election election;
    private ElectionOption option;
    private VoteRequest voteRequest;

    @BeforeEach
    void setUp() {
        voter = new Voter();
        voter.setId(1L);
        voter.setEmail("voter@example.com");

        election = new Election();
        election.setId(1L);
        election.setName("Test Election");
        election.setStatus(ElectionStatus.ACTIVE);
        election.setStartDate(LocalDateTime.now().minusDays(1));
        election.setEndDate(LocalDateTime.now().plusDays(1));

        option = new ElectionOption();
        option.setId(1L);
        option.setOptionTitle("Option 1");
        option.setElection(election);

        voteRequest = new VoteRequest();
        voteRequest.setElectionId(1L);
        voteRequest.setOptionId(1L);

        when(userDetails.getUsername()).thenReturn("voter@example.com");
    }

    @Test
    void castVote_ShouldSucceed_WhenValidRequest() {
        // Given
        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(electionOptionRepository.findById(1L)).thenReturn(Optional.of(option));
        when(votingRecordRepository.existsByVoterIdAndElectionId(1L, 1L)).thenReturn(false);

        // When
        voteService.castVote(voteRequest, userDetails);

        // Then
        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository).save(voteCaptor.capture());
        Vote savedVote = voteCaptor.getValue();
        assertThat(savedVote.getElection()).isEqualTo(election);
        assertThat(savedVote.getElectionOption()).isEqualTo(option);

        ArgumentCaptor<VotingRecord> recordCaptor = ArgumentCaptor.forClass(VotingRecord.class);
        verify(votingRecordRepository).save(recordCaptor.capture());
        VotingRecord savedRecord = recordCaptor.getValue();
        assertThat(savedRecord.getVoter()).isEqualTo(voter);
        assertThat(savedRecord.getElection()).isEqualTo(election);
    }

    @Test
    void castVote_ShouldThrowException_WhenVoterNotFound() {
        // Given
        when(voterRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteService.castVote(voteRequest, userDetails))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Voter not found");

        verify(voteRepository, never()).save(any());
        verify(votingRecordRepository, never()).save(any());
    }

    @Test
    void castVote_ShouldThrowException_WhenElectionNotFound() {
        // Given
        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteService.castVote(voteRequest, userDetails))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Election not found");

        verify(voteRepository, never()).save(any());
        verify(votingRecordRepository, never()).save(any());
    }

    @Test
    void castVote_ShouldThrowException_WhenElectionNotActive() {
        // Given
        election.setStatus(ElectionStatus.DRAFT);
        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));

        // When & Then
        assertThatThrownBy(() -> voteService.castVote(voteRequest, userDetails))
                .isInstanceOf(ElectionNotActiveException.class)
                .hasMessageContaining("Election is not active");

        verify(voteRepository, never()).save(any());
        verify(votingRecordRepository, never()).save(any());
    }

    @Test
    void castVote_ShouldThrowException_WhenVoterAlreadyVoted() {
        // Given
        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(votingRecordRepository.existsByVoterIdAndElectionId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> voteService.castVote(voteRequest, userDetails))
                .isInstanceOf(AlreadyVotedException.class)
                .hasMessageContaining("already voted");

        verify(voteRepository, never()).save(any());
        verify(votingRecordRepository, never()).save(any());
    }

    @Test
    void castVote_ShouldThrowException_WhenOptionNotFound() {
        // Given
        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(votingRecordRepository.existsByVoterIdAndElectionId(1L, 1L)).thenReturn(false);
        when(electionOptionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> voteService.castVote(voteRequest, userDetails))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Election option not found");

        verify(voteRepository, never()).save(any());
        verify(votingRecordRepository, never()).save(any());
    }

    @Test
    void castVote_ShouldThrowException_WhenOptionDoesNotBelongToElection() {
        // Given
        Election differentElection = new Election();
        differentElection.setId(2L);
        option.setElection(differentElection);

        when(voterRepository.findByEmail("voter@example.com")).thenReturn(Optional.of(voter));
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));
        when(votingRecordRepository.existsByVoterIdAndElectionId(1L, 1L)).thenReturn(false);
        when(electionOptionRepository.findById(1L)).thenReturn(Optional.of(option));

        // When & Then
        assertThatThrownBy(() -> voteService.castVote(voteRequest, userDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to the specified election");

        verify(voteRepository, never()).save(any());
        verify(votingRecordRepository, never()).save(any());
    }
}

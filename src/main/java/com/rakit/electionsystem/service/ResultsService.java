package com.rakit.electionsystem.service;

import com.rakit.electionsystem.dto.ElectionResultsResponse;
import com.rakit.electionsystem.dto.ElectionStatisticsResponse;
import com.rakit.electionsystem.dto.OptionResultResponse;
import com.rakit.electionsystem.exception.ResourceNotFoundException;
import com.rakit.electionsystem.model.Election;
import com.rakit.electionsystem.repository.ElectionRepository;
import com.rakit.electionsystem.repository.VoteRepository;
import com.rakit.electionsystem.repository.VotingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for retrieving election results and statistics.
 * Aggregates anonymous votes while maintaining voter privacy.
 */
@Service
@RequiredArgsConstructor
public class ResultsService {

    private final ElectionRepository electionRepository;
    private final VoteRepository voteRepository;
    private final VotingRecordRepository votingRecordRepository;

    /**
     * Gets complete results for an election including vote counts per option.
     *
     * @param electionId the election ID
     * @return election results with vote counts
     */
    @Transactional(readOnly = true)
    public ElectionResultsResponse getElectionResults(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        List<OptionResultResponse> optionResults = election.getOptions().stream()
                .map(option -> {
                    Long voteCount = voteRepository.countByElectionIdAndElectionOptionId(
                            electionId, option.getId()
                    );
                    return new OptionResultResponse(
                            option.getId(),
                            option.getOptionTitle(),
                            option.getDescription(),
                            voteCount
                    );
                })
                .collect(Collectors.toList());

        Long totalVotes = voteRepository.countByElectionId(electionId);

        return new ElectionResultsResponse(
                election.getId(),
                election.getName(),
                election.getDescription(),
                election.getStatus(),
                totalVotes,
                optionResults
        );
    }

    /**
     * Gets vote count for a specific election option.
     *
     * @param optionId the option ID
     * @return number of votes for this option
     */
    @Transactional(readOnly = true)
    public Long getOptionVoteCount(Long optionId) {
        return voteRepository.countByElectionOptionId(optionId);
    }

    /**
     * Gets comprehensive statistics for an election.
     *
     * @param electionId the election ID
     * @return election statistics including participation rate
     */
    @Transactional(readOnly = true)
    public ElectionStatisticsResponse getElectionStatistics(Long electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        Long totalVotes = voteRepository.countByElectionId(electionId);
        Long totalEligibleVoters = votingRecordRepository.countByElectionId(electionId);
        Long totalOptions = (long) election.getOptions().size();

        // Calculate participation rate (votes cast / eligible voters who participated)
        double participationRate = totalEligibleVoters > 0
                ? (totalVotes * 100.0) / totalEligibleVoters
                : 0.0;

        return new ElectionStatisticsResponse(
                election.getId(),
                election.getName(),
                election.getStatus(),
                totalVotes,
                totalEligibleVoters,
                totalOptions,
                participationRate,
                election.getStartDate(),
                election.getEndDate()
        );
    }
}

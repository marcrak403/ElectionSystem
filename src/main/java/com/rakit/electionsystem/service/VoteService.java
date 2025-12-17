package com.rakit.electionsystem.service;

import com.rakit.electionsystem.dto.VoteRequest;
import com.rakit.electionsystem.exception.AlreadyVotedException;
import com.rakit.electionsystem.exception.ElectionNotActiveException;
import com.rakit.electionsystem.exception.ResourceNotFoundException;
import com.rakit.electionsystem.model.Election;
import com.rakit.electionsystem.model.ElectionOption;
import com.rakit.electionsystem.model.Vote;
import com.rakit.electionsystem.model.Voter;
import com.rakit.electionsystem.model.VotingRecord;
import com.rakit.electionsystem.repository.ElectionOptionRepository;
import com.rakit.electionsystem.repository.ElectionRepository;
import com.rakit.electionsystem.repository.VoteRepository;
import com.rakit.electionsystem.repository.VoterRepository;
import com.rakit.electionsystem.repository.VotingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoterRepository voterRepository;
    private final ElectionRepository electionRepository;
    private final ElectionOptionRepository electionOptionRepository;
    private final VoteRepository voteRepository;
    private final VotingRecordRepository votingRecordRepository;

    @Transactional
    public void castVote(VoteRequest voteRequest, UserDetails currentUser) {
        Voter voter = voterRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found."));

        Election election = electionRepository.findById(voteRequest.getElectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + voteRequest.getElectionId()));

        if (!election.isActive()) {
            throw new ElectionNotActiveException("Election is not active.");
        }

        if (votingRecordRepository.existsByVoterIdAndElectionId(voter.getId(), election.getId())) {
            throw new AlreadyVotedException("Voter has already voted in this election.");
        }

        ElectionOption selectedOption = electionOptionRepository.findById(voteRequest.getOptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Election option not found with id: " + voteRequest.getOptionId()));

        if (!selectedOption.getElection().getId().equals(election.getId())) {
            throw new IllegalArgumentException("Selected option does not belong to the specified election.");
        }

        // Create the anonymous vote
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setElectionOption(selectedOption);
        voteRepository.save(vote);

        // Create the voting record to prevent double voting
        VotingRecord votingRecord = new VotingRecord();
        votingRecord.setVoter(voter);
        votingRecord.setElection(election);
        votingRecordRepository.save(votingRecord);
    }
}

package com.rakit.electionsystem.service;

import com.rakit.electionsystem.dto.ElectionOptionRequest;
import com.rakit.electionsystem.dto.ElectionOptionResponse;
import com.rakit.electionsystem.dto.ElectionRequest;
import com.rakit.electionsystem.dto.ElectionResponse;
import com.rakit.electionsystem.exception.ResourceNotFoundException;
import com.rakit.electionsystem.model.Election;
import com.rakit.electionsystem.model.ElectionOption;
import com.rakit.electionsystem.model.ElectionStatus;
import com.rakit.electionsystem.repository.ElectionOptionRepository;
import com.rakit.electionsystem.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionOptionRepository electionOptionRepository;

    @Transactional
    public ElectionResponse createElection(ElectionRequest electionRequest) {
        Election election = new Election();
        election.setName(electionRequest.getName());
        election.setDescription(electionRequest.getDescription());
        election.setStartDate(electionRequest.getStartDate());
        election.setEndDate(electionRequest.getEndDate());
        election.setCreatedAt(LocalDateTime.now());
        election.setStatus(ElectionStatus.DRAFT);

        Election savedElection = electionRepository.save(election);
        return mapToElectionResponse(savedElection);
    }

    @Transactional(readOnly = true)
    public List<ElectionResponse> getAllElections() {
        List<Election> elections = electionRepository.findAll();
        elections.forEach(election -> Hibernate.initialize(election.getOptions()));
        return elections.stream()
                .map(this::mapToElectionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ElectionResponse getElectionById(Long id) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        Hibernate.initialize(election.getOptions());
        return mapToElectionResponse(election);
    }

    @Transactional
    public ElectionResponse updateElectionStatus(Long id, ElectionStatus status) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + id));
        election.setStatus(status);
        Election updatedElection = electionRepository.save(election);
        Hibernate.initialize(updatedElection.getOptions());
        return mapToElectionResponse(updatedElection);
    }

    @Transactional
    public ElectionResponse addOptionToElection(Long electionId, ElectionOptionRequest optionRequest) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        ElectionOption option = new ElectionOption();
        option.setOptionTitle(optionRequest.getOptionTitle());
        option.setDescription(optionRequest.getDescription());
        option.setElection(election);

        electionOptionRepository.save(option);

        Election updatedElection = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));
        Hibernate.initialize(updatedElection.getOptions());

        return mapToElectionResponse(updatedElection);
    }

    private ElectionResponse mapToElectionResponse(Election election) {
        List<ElectionOptionResponse> optionResponses = election.getOptions().stream()
                .map(option -> new ElectionOptionResponse(option.getId(), option.getOptionTitle(), option.getDescription()))
                .collect(Collectors.toList());

        return new ElectionResponse(
                election.getId(),
                election.getName(),
                election.getDescription(),
                election.getStartDate(),
                election.getEndDate(),
                election.getStatus(),
                optionResponses
        );
    }
}

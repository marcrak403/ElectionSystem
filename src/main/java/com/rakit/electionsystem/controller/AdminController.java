package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.VoterResponse;
import com.rakit.electionsystem.model.Voter;
import com.rakit.electionsystem.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for admin-only operations.
 * All endpoints require ROLE_ADMIN authority.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final VoterService voterService;

    /**
     * Gets all voters in the system.
     *
     * @return list of all voters
     */
    @GetMapping("/voters")
    public ResponseEntity<List<VoterResponse>> getAllVoters() {
        List<Voter> voters = voterService.getAllVoters();
        List<VoterResponse> voterResponses = voters.stream()
                .map(this::mapToVoterResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(voterResponses);
    }

    /**
     * Gets a specific voter by ID.
     *
     * @param voterId the voter ID
     * @return voter details
     */
    @GetMapping("/voters/{voterId}")
    public ResponseEntity<VoterResponse> getVoterById(@PathVariable Long voterId) {
        Voter voter = voterService.getVoterById(voterId);
        VoterResponse response = mapToVoterResponse(voter);
        return ResponseEntity.ok(response);
    }

    /**
     * Activates a voter account.
     *
     * @param voterId the voter ID
     * @return success message
     */
    @PatchMapping("/voters/{voterId}/activate")
    public ResponseEntity<VoterResponse> activateVoter(@PathVariable Long voterId) {
        Voter voter = voterService.activateVoter(voterId);
        VoterResponse response = mapToVoterResponse(voter);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivates a voter account.
     *
     * @param voterId the voter ID
     * @return success message
     */
    @PatchMapping("/voters/{voterId}/deactivate")
    public ResponseEntity<VoterResponse> deactivateVoter(@PathVariable Long voterId) {
        Voter voter = voterService.deactivateVoter(voterId);
        VoterResponse response = mapToVoterResponse(voter);
        return ResponseEntity.ok(response);
    }

    /**
     * Maps a Voter entity to a VoterResponse DTO.
     *
     * @param voter the voter entity
     * @return voter response DTO
     */
    private VoterResponse mapToVoterResponse(Voter voter) {
        return new VoterResponse(
                voter.getId(),
                voter.getEmail(),
                voter.getFirstName(),
                voter.getLastName(),
                voter.getPesel(),
                voter.getRole(),
                voter.getActive(),
                voter.getCreatedAt(),
                voter.getUpdatedAt()
        );
    }
}

package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.ElectionResultsResponse;
import com.rakit.electionsystem.dto.ElectionStatisticsResponse;
import com.rakit.electionsystem.service.ResultsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for election results and statistics endpoints.
 * Results are aggregated from anonymous votes.
 */
@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultsController {

    private final ResultsService resultsService;

    /**
     * Gets the complete results for a specific election.
     * Accessible by authenticated users.
     *
     * @param electionId the election ID
     * @return election results with vote counts per option
     */
    @GetMapping("/elections/{electionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ElectionResultsResponse> getElectionResults(@PathVariable Long electionId) {
        ElectionResultsResponse results = resultsService.getElectionResults(electionId);
        return ResponseEntity.ok(results);
    }

    /**
     * Gets comprehensive statistics for a specific election.
     * Restricted to admin users only.
     *
     * @param electionId the election ID
     * @return election statistics including participation metrics
     */
    @GetMapping("/elections/{electionId}/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionStatisticsResponse> getElectionStatistics(@PathVariable Long electionId) {
        ElectionStatisticsResponse statistics = resultsService.getElectionStatistics(electionId);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Gets the vote count for a specific election option.
     * Accessible by authenticated users.
     *
     * @param optionId the election option ID
     * @return vote count
     */
    @GetMapping("/options/{optionId}/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getOptionVoteCount(@PathVariable Long optionId) {
        Long voteCount = resultsService.getOptionVoteCount(optionId);
        return ResponseEntity.ok(voteCount);
    }
}

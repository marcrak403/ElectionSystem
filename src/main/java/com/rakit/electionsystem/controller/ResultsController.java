package com.rakit.electionsystem.controller;

import com.rakit.electionsystem.dto.ElectionResultsResponse;
import com.rakit.electionsystem.dto.ElectionStatisticsResponse;
import com.rakit.electionsystem.dto.ErrorResponse;
import com.rakit.electionsystem.service.ResultsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Results", description = "Election results and statistics endpoints")
@SecurityRequirement(name = "JWT Bearer Token")
public class ResultsController {

    private final ResultsService resultsService;

    /**
     * Gets the complete results for a specific election.
     * Accessible by authenticated users.
     *
     * @param electionId the election ID
     * @return election results with vote counts per option
     */
    @Operation(summary = "Get election results",
               description = "Returns complete election results with vote counts for each option. Requires authentication.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Results retrieved successfully",
                     content = @Content(schema = @Schema(implementation = ElectionResultsResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Election not found",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "Get election statistics (Admin only)",
               description = "Returns comprehensive statistics including participation rate and voter count. Admin access required.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                     content = @Content(schema = @Schema(implementation = ElectionStatisticsResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - admin role required",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Election not found",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
